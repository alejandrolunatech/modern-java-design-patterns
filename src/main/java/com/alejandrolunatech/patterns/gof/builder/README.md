# Builder

**Problem.** An email has two required parts and several optional ones. A constructor with six parameters, some of them `null`, is easy to call wrongly.

**The code.** `EmailMessage` is a record that validates itself and copies its lists. The builder only collects values and calls the record's constructor, so a built message is always valid and immutable.

**What modern Java changes.** Records give equals, hashCode and toString for free, and the compact constructor is the single place for validation. Many classes that used to need a builder are now a record with two or three fields.

**When not to use it.** When every field is required or there are only a few. Then the record's constructor is clearer than a builder.

**Tests:** [`EmailMessageTest`](/src/test/java/com/alejandrolunatech/patterns/gof/builder/EmailMessageTest.java)
