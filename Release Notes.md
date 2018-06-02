# Release notes for 0.16.10.0

`PageLoading` and `PageReady` messages sent via `MessageBus` - primarily to assist with testing but could also be used for application monitoring
`UIKey` uses UUID instead of sequential count from `UIKeyProvider`.  `UIKeyProvider` removed as now redundant
Made headerRow accessible in sub-classes of `DefaultApplicationUI`