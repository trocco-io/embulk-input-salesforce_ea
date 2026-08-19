# Salesforce Ea file input plugin for Embulk

TODO: Write short description here and build.gradle file.

## Overview

* **Plugin type**: file input
* **Resume supported**: yes
* **Cleanup supported**: yes

## Configuration

### Authentication

- **auth_method**: Authentication method (`oauth` or `user_password`, default: `"user_password"`)

**For OAuth authentication:**
- **access_token**: OAuth access token (required when `auth_method` is `oauth`)
- **instance_url**: Salesforce instance URL (required when `auth_method` is `oauth`, e.g., `https://your-instance.salesforce.com`)

**For user/password authentication:**
- **username**: Salesforce username (required when `auth_method` is `user_password`)
- **password**: Salesforce password (required when `auth_method` is `user_password`)
- **security_token**: Salesforce security token (optional, may be required depending on your Salesforce org settings)
- **auth_end_point**: Salesforce authentication endpoint (string, default: `"https://login.salesforce.com/services/Soap/u/"`)

### API Settings

- **api_version**: Salesforce API version (string, default: `"64.0"`)
- **connection_timeout**: Connection timeout in milliseconds (string, default: `"600000"`)

### Data Query

- **dataset_id**: Einstein Analytics dataset ID (required)
- **saql**: SAQL (SOQL Analytics Query Language) query to retrieve data (required)
- **pages**: Number of pages to retrieve (string, default: `1`)
- **step**: Number of rows to retrieve per page (string, default: `10000`)

## Example

```yaml
in:
  type: salesforce_ea
  access_token: "Access Token"
  instance_url: "Salesforce instance url"
  auth_method: "oauth"
  dataset_id: "dataset id"
  saql: |
    q = load "SAMPLE";
    q = group q by all;
    q = foreach q generate count(q) as 'A';
  parser:
    type: json
    charset: UTF-8
    newline: CRLF
    delimiter: ','
    columns: [ ]
out:
  type: stdout
```


## Build

```
$ ./gradlew gem  # -t to watch change of files and rebuild continuously
```
