# Welcome to Web Spring Coffee
***

## Task

This project takes the coffee shop ordering app, already rebuilt once in plain Java and again
on Spring Boot in an earlier step of this program, and answers the next question in the
assignment: "I want my Coffee on the Web, FASTER!" That meant three concrete things, all
delivered in this one repository.

- **Spring MVC on top.** A browser-facing storefront built with Thymeleaf views: a menu page to
  place an order, an order history page, a barista console, and a chat page, all served by
  ordinary `@Controller` classes rather than a REST API a human is expected to curl by hand.
- **Hibernate and JPA underneath.** `CoffeeOrder`, `Coffee`, and `ChatMessage` are real JPA
  entities, persisted through Spring Data JPA against MySQL, with the schema kept up to date by
  Hibernate itself rather than hand-written migrations.
- **A WebSocket for the chat, not a poll.** The earlier Spring Boot version of this app's chat
  worked by polling. This one does not: a customer and the shared barista console both open a
  raw WebSocket at `/ws/chat`, and a message, or an order going from placed to paid to ready,
  shows up on the other side the instant it happens, with no refresh and no interval timer
  involved anywhere.

All ten GoF design patterns from the earlier submissions are here again too, in the sixteen
packages the assignment asks for, but this is a fresh, standalone implementation rather than a
copy of the previous project with a web layer bolted on. Reusing the design knowledge from the
earlier submissions was fine; reusing the code itself was not the point of the exercise.

## Description

### The pieces

| Package | What it does |
|---|---|
| `controller` | `CoffeeShopController` (menu, place order, order history), `ChatPageController` (the chat page for one order), `BaristaController` (login, console, pay/fulfill/cancel) |
| `service`, `repository`, `model` | the application layer and the three JPA entities (`CoffeeOrder`, `Coffee`, `ChatMessage`) behind them |
| `config` | `WebMvcConfig` wiring two `HandlerInterceptor`s: one that hands every browser session a customer id on first visit, one that gates the barista console behind the login form |
| `websocket` | `WebSocketConfig` registering `ChatWebSocketHandler` at `/ws/chat`, routed per order id from the connection's own query string, with no message broker in between |
| `factory`, `decorator`, `strategy`, `singleton`, `observer`, `command`, `adapter`, `facade`, `prototype`, `template` | the ten design patterns, each one a real, working piece of the running app rather than a textbook example sitting off to the side |

### How an order flows through the app

1. A customer opens `/`, which `CoffeeShopController.home` renders from `MenuService`'s menu
   (built once at startup by the Factory pattern) together with the list of available extras.
2. Submitting the order form posts to `/order`. `CoffeeShopFacade.placeOrder(...)` is the only
   way an order is ever placed: it runs the Strategy pattern to price the order (regular or
   happy-hour, by the clock), stamps it with a daily order code minted by the Singleton, and
   saves it through the Command pattern's `PlaceOrderCommand`.
3. Placing the order redirects straight to `/chat/{orderId}`, which opens a WebSocket connection
   to `/ws/chat?orderId={orderId}` and joins that order's chat room.
4. The barista console at `/barista/console` lists every active order. Paying, fulfilling, or
   cancelling an order posts to `/barista/orders/{id}/...`, each one running through the same
   Facade and a Command of its own.
5. Fulfilling an order fires the Observer pattern: `ChatOrderStatusObserver` pushes a system
   message straight down the WebSocket to anyone with that order's chat open, and
   `OrderMetricsObserver` bumps a running fulfilled-order counter shown on the barista console.
   Both observers run off the same status change; neither has to poll anything to find out about
   it.
6. From the order history page, "order it again" posts to `/orders/{id}/reorder`, which clones
   the past order through the Prototype pattern and runs it through `PlaceOrderCommand` as a
   brand new order with a fresh code.

### Design notes

- **The facade is the only door.** Every order lifecycle transition, whatever controller it
  comes from, goes through `CoffeeShopFacade`, never through a repository or service call made
  directly from a controller.
- **Customer identity is a receipt number, not a login.** `CustomerIdentificationInterceptor`
  hands a new browser session a customer id from the same daily-sequence Singleton that mints
  order codes, the first time it shows up. There is nothing to sign up for and nothing to type
  in; the only thing distinguishing one customer from another is which browser session they are
  using.
- **Barista login is intentionally minimal.** The assignment marks Spring Security as optional,
  and a single shared `barista` / `coffee123` credential pair checked by `BaristaAuthService`,
  gated by a plain `HandlerInterceptor`, covers exactly what this app needs: keeping the console
  away from customers, nothing more. Adding Spring Security on top would be solving a problem
  this assignment does not actually have.
- **The WebSocket room is keyed by order id, not by any user id.** This app assumes one shared
  barista console watching every order at once, so a message sent into an order's room reaches
  every open session for that order, customer and barista alike, with no separate routing per
  participant.
- **A handful of known limitations exist on purpose:**
  - There is no idempotency key on placing an order, so retrying the exact same form submission
    can place a second order.
  - Cancelling an order that has already been paid for does not trigger a refund.
  - The barista credential pair is shared and hardcoded; there is no per-barista account or
    audit trail of which barista acted on an order.
  - A WebSocket session that reconnects after a drop re-fetches nothing on its own; the chat
    page's own history load on page open is what fills it back in.

## Installation

You will need **JDK 25 or newer**, **Maven**, and **Docker**. MySQL always runs inside a
container, both for the app itself and for the test suite, so there is no in-memory substitute
standing in anywhere for the real database this app runs against.

```bash
git clone https://github.com/devbossma/web-spring-coffee.git
cd web-spring-coffee
mvn clean install
```

## Usage

### Running the app

```bash
docker compose up -d
mvn spring-boot:run
```

`docker compose up -d` starts a MySQL container seeded with an empty `coffeeshop` database on
port 3306, matching `application.properties` exactly. Hibernate creates and updates the schema
itself on startup (`spring.jpa.hibernate.ddl-auto=update`), and `MenuSeeder` inserts the six menu
items the first time it runs against an empty `coffees` table.

Once it is up, open **http://localhost:8080** in a browser to order coffee as a customer, and
**http://localhost:8080/barista** in another tab (or another browser) to log in as the barista
with `barista` / `coffee123` and watch orders, and chat messages, arrive in real time.

### Trying it out end to end

1. On the customer tab, pick a coffee, add an extra or two, and submit the order. You land on
   that order's chat page immediately.
2. On the barista tab, open the console. The new order appears without a refresh being needed,
   since the page's own WebSocket connection pushes it in.
3. Click into the order on the barista console, send a chat message, and watch it appear on the
   customer's chat page instantly.
4. Pay, then fulfill, the order from the barista console. The customer's chat page receives a
   system message the moment it is fulfilled, with no polling involved.
5. Back on the customer side, visit `/orders` and use "order it again" on a past order. It places
   a brand new order, cloned from the old one, and drops you straight into its own fresh chat.

### Running the tests

```bash
mvn clean verify
```

This needs Docker running, because the test suite starts its own disposable MySQL container
through Testcontainers rather than using any in-memory stand-in. The build does two things at
once: it runs the entire test suite, and it enforces an 80 percent minimum line coverage floor
for every package, so a shrinking test suite fails the build loudly instead of slipping through
quietly. The suite currently stands at 80 tests, all of them passing.

Use `verify`, not `test`, when you actually want to know whether the build is sound. `mvn test`
only runs the suite and writes a coverage report to disk; it does not fail the build if coverage
has dropped below the floor.

### The Core Team


<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School's Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>
