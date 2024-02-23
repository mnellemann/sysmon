# Changelog

All notable changes to this project will be documented in this file.

## [1.1.6] - 2024-01-31
- Allow server to override timestamp from clients
- Update 3rd party dependencies

## [1.1.5] - 2023-12-12
- Update 3rd party dependencies

## [1.1.2] - 2023-02-06
- Lowercase client hostnames

## [1.1.1] - 2023-01-22
- Simplify plugin naming
- Initial support for executing (groovy) scripts
- Fixed bug when no config file were found
- Update the default [dashboards](doc/dashboards/)

## [1.1.0] - 2022-12-17
- Lower influx time precision from milliseconds to seconds
  - requires you to update server and clients to this version.
- Update *oshi* dependency (for AIX improvements).


## [1.0.24] - 2022-11-16
- Fix incorrect use of OSHI getDiskStores()
- Update dashboards

## [1.0.23] - 2022-11-07
- Update dashboards.
- Lower default interval for most plugins.
- Simplify metrics-results to influx points code.
- Remove logging of skipped disk devices (eg. cd0).

## [1.0.21] - 2022-10-30
- Update dashboard
- Add IP connections

## [1.0.18] - 2022-10-24
- Bump version to 1.x to indicate stable release.
- Update 3rd party dependencies.

## [0.1.13] - 2022-06-27

## [0.1.11] - 2022-03-02
### Changed
- (plugins) Removed groovy dependency from build.gradle (it increased size and was not needed).

## [0.1.10] - 2022-03-01
### Added
- (client) More debug options.
- (plugins/linux) Re-enabled network socket-statistics extension.
### Changed
- Updated the oshi dependency to v. 6.1.4.
- (plugins/aix) Improved AIX lparstat parsing.
- (plugins/aix) More debug output from (Power) processor extension.
- (plugins/base) More debug output from plugins-base disk extension.

## [0.1.9] - 2022-02-15
### Changed
- Updated 3rd party dependencies.

<!--
[1.1.0]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v1.1.0%0Dv0.1.24
[1.0.24]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v1.0.24%0Dv0.1.23
[1.0.23]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v1.0.23%0Dv0.1.21
[1.0.21]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v1.0.21%0Dv0.1.18
[1.0.18]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v1.0.18%0Dv0.1.13
[0.1.13]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v0.1.13%0Dv0.1.11
[0.1.11]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v0.1.11%0Dv0.1.10
[0.1.10]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v0.1.10%0Dv0.1.9
[0.1.9]: https://bitbucket.org/mnellemann/sysmon/branches/compare/v0.1.9%0Dv0.1.8
-->
