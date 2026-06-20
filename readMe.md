# Milk Collection Manager

A mobile-first Android application designed to help milk collectors manage daily milk collection from farmers, calculate weekly totals, generate payment reports, and maintain historical records.

---

# Overview

Milk Collection Manager replaces traditional paper notebooks used by milk collectors.

The application allows collectors to:

* Register and manage farmers
* Record milk collection twice per day (Morning & Evening)
* Track weekly milk totals per farmer
* Calculate payments using a configurable milk unit price
* Access historical records and reports
* Export payment summaries and collection reports

The app is designed to work offline and be simple enough to use in rural areas with limited internet access.

---

# Problem Statement

Milk collectors typically record milk quantities manually in notebooks.

Common challenges include:

* Manual calculations
* Errors in weekly totals
* Time-consuming payment calculations
* Difficulty accessing historical records
* Loss or damage of paper records
* Inconsistent weekly reporting

This application digitizes the entire workflow while preserving the simplicity of the traditional process.

---

# Key Features

## Farmer Management

* Add new farmers
* Edit farmer information
* Remove inactive farmers
* Store contact information
* Store notes and remarks
* Search farmers quickly

---

## Daily Collection Recording

Record milk collection twice daily:

### Morning Collection

* Enter quantity for each farmer
* Save collection records
* Edit entries later if needed

### Evening Collection

* Enter quantity for each farmer
* Save collection records
* Edit entries later if needed

---

## Weekly Tracking

The system automatically:

* Tracks milk quantities per farmer
* Calculates weekly totals
* Displays collection progress throughout the week
* Maintains separate records for morning and evening sessions

---

## Payment Calculation

The collector can define a milk price per liter.

The system automatically calculates:

```text
Farmer Payment = Total Milk × Unit Price
```

Example:

```text
Milk Collected = 150 Liters
Unit Price = 18 EGP

Payment = 150 × 18

Total = 2700 EGP
```

---

## Weekly Reports

Generate reports showing:

* Farmer name
* Weekly milk total
* Unit price
* Amount payable
* Overall collection totals

---

## Weekly Archive

At the end of every week:

* Current week is archived
* Historical data is preserved
* New week begins automatically
* Farmer records remain available

Archived weeks can be viewed at any time.

---

## Offline First

The application is designed for areas with unreliable internet.

Features:

* Local database storage
* No internet required for daily operation
* Fast performance

---

# User Roles

## Milk Collector

The application currently supports a single role:

### Collector

Responsibilities:

* Manage farmers
* Record milk collection
* View reports
* Archive weeks
* Manage settings

Future versions may support:

* Dairy managers
* Supervisors
* Multiple collectors

---

# Application Workflow

## Initial Setup

1. Install application
2. Open app
3. Configure unit price
4. Add farmers

---

## Daily Workflow

### Morning

1. Open app
2. Select Morning Collection
3. Enter milk quantity for each farmer
4. Save

### Evening

1. Open app
2. Select Evening Collection
3. Enter milk quantity for each farmer
4. Save

---

## During the Week

Collector can:

* View weekly totals
* View farmer balances
* Monitor collection performance

---

## Friday Evening Workflow

1. Open Weekly Summary
2. Review totals
3. Confirm archive
4. Start new week

---

# Screens

## Dashboard

Displays:

* Current week information
* Today's morning total
* Today's evening total
* Weekly collection total
* Current milk price
* Quick action buttons

---

## Collection Entry Screen

Displays:

* Farmer list
* Milk quantity input field
* Search functionality
* Save button

Supports:

* Morning session
* Evening session

---

## Farmers Screen

Displays:

* List of farmers
* Add farmer

---

## Farmer Details Screen

Displays:

* Farmer information
* Weekly milk totals
* Payment calculation
* Historical collection records

---

## Reports Screen

Displays:

* Weekly collection report
* Farmer payment report
* Total liters collected
* Total payable amount

Supports:

* PDF export

---

## Archive Screen

Displays:

* Previous weeks

---

## Settings Screen

Allows:

* Change milk unit price

---

# Functional Requirements

## Farmer Management

### Add Farmer

Required:

* Name

Optional:

* Phone number
* Notes

---

### Remove Farmer

Collector can remove inactive farmers.

Historical records must remain intact.

---

## Reporting

System provides:

* Daily reports
* Weekly reports
* Farmer reports
* Archived reports

# Technology Stack

## Frontend

Android - Kotlin

---

## Local Database

SQLite

Recommended package:

```text
Room
```

---

# License

This project is intended for agricultural and dairy collection management.

License:

MIT License

