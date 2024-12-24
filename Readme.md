# Test Task

## Overview
Crypto Fetcher Service that retrieves cryptocurrency rates from two different suppliers, each with its own JSON format. The service normalizes the data from these suppliers and provides a unified JSON response containing the suppliers and their respective currencies.

If a supplier does not provide data, the service fetches previously saved data from a PostgreSQL database. In case neither the suppliers nor the database provide data, the service returns a JSON response with the list of connected suppliers.

## Features
- Easy extensibility: New suppliers can be added as beans with their specific requirements (e.g., API keys).
- Fault-tolerant: Handles scenarios where supplier data is unavailable by falling back to cached database entries.

## Tech Stack
- **Spring Flux & Spring Data**: For reactive programming and data handling.
- **Liquibase**: For database migration.
- **PostgreSQL**: For data persistence.
- **TestContainers**: To Mock PG in IT test

## Required Application Variables
The application requires specific variables to function correctly. These variables should be configured in your environment. See the `VM.txt` file for the full list of variables and their descriptions.

## How to Add a New Supplier
1. **Set up a new supplier bean**: Configure the supplier as a Spring bean.
2. **Expand `@JsonCreator` in `CurrencyRateDto`**: If needed, map the supplier's data format to the DTO.

