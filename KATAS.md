# Katas: rebuild a pattern from its tests

Reading a pattern is not the same as writing it under pressure. These katas are practice for live-coding rounds: delete a pattern's code, keep its tests, and make them pass again.

## The three katas

| Pattern | Tests to make pass | Files to delete |
| --- | --- | --- |
| Strategy | `ShippingCostTest` | `ShippingCost.java`, `Checkout.java` (keep `Parcel.java`) |
| Observer | `EventBusTest` | `EventBus.java`, `Subscription.java` (keep `OrderEvent.java`) |
| Command | `EditorTest` | `Edit.java`, `Editor.java` |

Time-box each one to 30 to 45 minutes, without AI help and without looking at the deleted code.

## How

1. Start a branch: `git switch -c kata/strategy`
2. Delete the files listed above for that pattern.
3. Run only its tests, and write code until they pass: `./mvnw test -Dtest=ShippingCostTest`
4. Run everything, so the architecture rules still hold: `./mvnw verify`
5. Compare your version with the original: `git diff main -- src/main`
6. Write one line in [docs/LEARNING-NOTES.md](docs/LEARNING-NOTES.md) on what you would do differently. Then throw the branch away: `git switch main && git branch -D kata/strategy`

## Done so far

| Kata | Date | Time taken | Notes |
| --- | --- | --- | --- |
| Strategy | | | |
| Observer | | | |
| Command | | | |
