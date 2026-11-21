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
  STOMP connection over WebSocket at `/ws/chat`, and a message, or an order going from placed to
  paid to ready, shows up on the other side the instant it happens, with no refresh and no
  interval timer involved anywhere.

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
| `config` | `WebMvcConfig` wiring two `HandlerInterceptor`s: one that hands every browser session a customer id on first visit, one that gates the barista console behind the login form; `PasswordConfig` exposes the bcrypt `PasswordEncoder` bean |
| `websocket` | `WebSocketConfig` enabling a simple STOMP broker over `/ws/chat`; `ChatStompController`'s `@MessageMapping("/chat/{orderId}")` handles inbound chat messages and broadcasts to `/topic/chat/{orderId}`, the destination every session for that order subscribes to |
| `factory`, `decorator`, `strategy`, `singleton`, `observer`, `command`, `adapter`, `facade`, `prototype`, `template` | the ten design patterns, each one a real, working piece of the running app rather than a textbook example sitting off to the side |
| `tools` | `HashGenerator`, a standalone CLI for generating the bcrypt hash that `BARISTA_PASSWORD_HASH` needs; not part of the running app, so it is excluded from the coverage gate the same way the application's own entry point is |

### How an order flows through the app

1. A customer opens `/`, which `CoffeeShopController.home` renders from `MenuService`'s menu
   (built once at startup by the Factory pattern) together with the list of available extras.
2. Submitting the order form posts to `/order`. `CoffeeShopFacade.placeOrder(...)` is the only
   way an order is ever placed: it runs the Strategy pattern to price the order (regular or
   happy-hour, by the clock), stamps it with a daily order code minted by the Singleton, and
   saves it through the Command pattern's `PlaceOrderCommand`.
3. Placing the order redirects straight to `/chat/{orderId}`, which opens a STOMP connection to
   `/ws/chat` and subscribes to `/topic/chat/{orderId}`, that order's chat room.
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
  so instead of pulling in the full framework this app uses only `spring-security-crypto` for
  bcrypt hashing. A single shared `barista` account, checked by `BaristaAuthService` against a
  bcrypt hash and gated by a plain `HandlerInterceptor`, covers exactly what this app needs:
  keeping the console away from customers, nothing more. The real password is never hardcoded or
  committed; see [Setting the barista password](#setting-the-barista-password) below for how to
  generate and set it. `BaristaAuthService` also runs a dummy bcrypt comparison for unknown
  usernames, so an unknown username and a wrong password both take the same amount of time,
  closing the timing gap that would otherwise let someone enumerate valid usernames.
- **The chat "room" is a STOMP destination keyed by order id, not by any user id.** This app
  assumes one shared barista console watching every order at once, so a message published to
  `/topic/chat/{orderId}` reaches every session subscribed to it, customer and barista alike,
  with no separate routing per participant.
- **`chat.js` speaks STOMP without a library.** Rather than pull in a CDN-hosted STOMP client
  when every other script in this app has zero dependencies, `chat.js` hand-writes the handful
  of STOMP 1.2 frames (`CONNECT`, `SUBSCRIBE`, `SEND`, `MESSAGE`) it actually needs, directly
  over the same `WebSocket` object a raw handler would have used.
- **A handful of known limitations exist on purpose:**
  - There is no idempotency key on placing an order, so retrying the exact same form submission
    can place a second order.
  - Cancelling an order that has already been paid for does not trigger a refund.
  - The barista account is shared, one username for every barista; there is no per-barista
    account or audit trail of which barista acted on an order.
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

### Setting the barista password

The barista account's password is never hardcoded or committed. `BaristaAuthService` reads a
bcrypt hash from the `BARISTA_PASSWORD_HASH` environment variable
(`application.properties` wires it in as `barista.password-hash=${BARISTA_PASSWORD_HASH}`), so
the app will not start until that variable is set. `HashGenerator`, a small CLI in the `tools`
package, generates the hash for you:

```bash
mvn compile org.codehaus.mojo:exec-maven-plugin:3.5.1:java \
    -Dexec.mainClass=dev.saberlabs.springcoffee.tools.HashGenerator \
    -Dexec.args="your-chosen-password" -Dexec.classpathScope=compile
```

On Windows `cmd.exe`, use `^` instead of `\` to continue a line, or just put it all on one line.
This is the real output from running that command, with your own chosen password in place of
`your-chosen-password`:

```
C:\Users\pc\IdeaProjects\web-spring-coffee>mvn compile org.codehaus.mojo:exec-maven-plugin:3.5.1:java ^
More?     -Dexec.mainClass=dev.saberlabs.springcoffee.tools.HashGenerator ^
More?     -Dexec.args="your-chosen-password" -Dexec.classpathScope=compile
[INFO] Scanning for projects...
[INFO]
[INFO] ------------------< dev.saberlabs:web-spring-coffee >-------------------
[INFO] Building WebSpringCoffee 0.1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- jacoco:0.8.15:prepare-agent (prepare-agent) @ web-spring-coffee ---
[INFO] argLine set to -javaagent:C:\\Users\\pc\\.m2\\repository\\org\\jacoco\\org.jacoco.agent\\0.8.15\\org.jacoco.agent-0.8.15-runtime.jar=destfile=C:\\Users\\pc\\IdeaProjects\\web-spring-coffee\\target\\jacoco.exec
[INFO]
[INFO] --- resources:3.5.0:resources (default-resources) @ web-spring-coffee ---
[INFO] Copying 1 resource from src\main\resources to target\classes
[INFO] Copying 7 resources from src\main\resources to target\classes
[INFO]
[INFO] --- compiler:3.15.0:compile (default-compile) @ web-spring-coffee ---
[INFO] Nothing to compile - all classes are up to date.
[INFO]
[INFO] --- exec:3.5.1:java (default-cli) @ web-spring-coffee ---
$2a$10$lGBfbqBV.tBQCweQML5S0e/A4Fw3iV/fV01Nfhjiub2y4hsZh3fE2
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.016 s
[INFO] Finished at: 2026-11-15T02:07:15+01:00
[INFO] ------------------------------------------------------------------------
```

The `$2a$10$lGBfbqBV...` line in the middle of all that Maven log output is the hash. Everything
else is normal build noise from `exec-maven-plugin` running through the same build as the rest
of the project; it is not an error.

Copy that hash line and set it as `BARISTA_PASSWORD_HASH` before starting the app:

```bash
export BARISTA_PASSWORD_HASH='<the hash HashGenerator printed>'
```

On Windows PowerShell: `$env:BARISTA_PASSWORD_HASH = '<the hash>'`. In IntelliJ, set it as an
environment variable on the run configuration instead. The variable only needs to be set once
per shell or run configuration; the tests never depend on it; `AbstractIntegrationTest` supplies
its own fixed hash directly to the Spring context instead.

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
with username `barista` and whichever password you hashed into `BARISTA_PASSWORD_HASH` above,
and watch orders, and chat messages, arrive in real time.

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
quietly. The suite currently stands at 97 tests, all of them passing. `BARISTA_PASSWORD_HASH`
does not need to be set to run this: `AbstractIntegrationTest` supplies its own hash directly to
the Spring context, so the test suite never depends on anything in your shell environment.

Use `verify`, not `test`, when you actually want to know whether the build is sound. `mvn test`
only runs the suite and writes a coverage report to disk; it does not fail the build if coverage
has dropped below the floor.

### The Core Team


<span><i>Made at <a href='https://qwasar.io'>Qwasar SV -- Software Engineering School</a></i></span>
<span><img alt='Qwasar SV -- Software Engineering School's Logo' src='https://storage.googleapis.com/qwasar-public/qwasar-logo_50x50.png' width='20px' /></span>
