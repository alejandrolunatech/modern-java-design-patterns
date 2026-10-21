# Adapter

**Problem.** An old device client returns XML in Fahrenheit or Celsius. The rest of the system wants a `Temperature` in Celsius.

**The code.** `LegacyThermostatAdapter` implements the interface we want (`TemperatureSensor`) and translates the old format in one place. An unknown format fails loudly instead of producing a wrong number.

**What modern Java changes.** Records make the target type a one-liner, and the legacy client can be a lambda in tests.

**When not to use it.** When you own both sides. Then change one of them instead of adding a translation layer.

**Tests:** [`LegacyThermostatAdapterTest`](/src/test/java/com/alejandrolunatech/patterns/gof/adapter/LegacyThermostatAdapterTest.java)
