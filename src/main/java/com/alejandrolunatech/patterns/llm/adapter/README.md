# Adapter for model providers

**Problem.** Each vendor SDK has its own shape: chat messages with usage numbers, or a plain text API with none.

**The code.** Two made-up SDKs in `VendorSdks`, each with an adapter to `ModelClient`. The chat adapter maps roles and turns the vendor's overload error into our `ModelUnavailableException`. The text adapter folds the system prompt in and marks its token counts as estimated.

**What modern Java changes.** Nothing new in the language; the point is where the boundary sits. Vendor types never leave the adapter.

**When not to use it.** When a framework such as Spring AI already gives you a vendor-neutral client. Then adapt that one interface instead of each SDK.

**Tests:** [`AdaptersTest`](/src/test/java/com/alejandrolunatech/patterns/llm/adapter/AdaptersTest.java)
