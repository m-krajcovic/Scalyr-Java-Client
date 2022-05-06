# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [7.0.0] - 2022-05-05
### Added
- Ability to set compression method directly using `Events.setCompressionType(EventUploader.CompressionType compressionType)`
- Support for the ZStandard compression method, which provides compression ratio comparable to gzip, but faster. You can turn it on by calling `Events.setCompressionType(EventUploader.CompressionType.Zstandard)`. Gzip is still the default compression method.

### Removed
- Support for the legacy java.net.HttpURLConnection-based HTTP client. The option `useApacheHttpClientForEventUploader` has been removed, and the ApacheHttpClient is now the default (and currently only) option.
- Support for enabling and disabling Gzip though `Events.enableGzip` and `Events.disableGzip`. Use the new `Events.setCompressionType` method instead.
