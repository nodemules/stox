# Node Mules Stox API

This API will serve stock information

## Objectives

* Serve real-time data
* Serve analytical data
* Calculate trends

## Dependencies

* requires a
  valid `firebase.json` [configuration file](https://support.google.com/firebase/answer/7015592) in
  the Project Root
* requires an Environment Variable `FIREBASE_DATABASE_URL` which should match the `databaseUrl` property in `firebase.json`
* requires an Environment Variable `ALPHA_VANTAGE_API_KEY`
  from [AlphaVantage.co](https://www.alphavantage.co/support/#api-key)
* requires an Environment Variable `YAHOO_FINANCE_API_KEY` from [RapidAPI](https://rapidapi.com/apidojo/api/yahoo-finance1)
* requires `redis` (`docker run -d -p6379:6379 redis`)
