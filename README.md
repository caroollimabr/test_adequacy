# WebTeV: an adequacy approach for automated E2E web tests

This is the tool that puts into practice the multi-dimensional test adequacy approach for automated E2E web tests from the paper **Towards a Test Adequacy Approach for Automated E2E Web Tests**.

## 1. Project Overview
* **Programming language:** JavaScript
* **Runtime environment:** Node.js
* **Libraries and modules:** Chalk, Path, Fs-extra, Pdf-parse, and Yaml

## 2. Prerequisites
Before starting, make sure you have installed:
* Visual Studio Code;
* [Node.js](https://nodejs.org/) (v16 or higher);
* Package manager (npm or yarn).

## 3. After cloning the project
In the command line,
* Run `npm install` to install node_modules;
* Run `npm link` in order to test the tool.

## 4. Execution and Reports
In the command line,
* Access the automated E2E web test project folder;
* Run `webtev concov --openapi=<openapi_file.json or openapi_file.yaml> --har=<.log_folder>` to create a contract coverage report;
* Run `  webtev reqcov <requirements_file.pdf> <test_folder>` to create a repeatable requirements coverage report.

# SpringEspressoE2E: a Playwright automation

This is the automated E2E test suite using Playwright for the project SpringEspresso, focusing on traceability (HAR) and data integrity (Database Cleanup). In preliminary tests, the tool WebTeV was used to provide both contract coverage and repeatable requirements reports.

**Contract Coverage Flow**
* HAR logs location: ./har_logs
* SpringEspresso's OpenAPI specification (API contract): ./openapi/openapi.json and ./openapi/openapi.yaml
* Contract Coverage Report: ./concov_report/report.html

**Repeatable Requirements Coverage Flow**
* Requirements specification file: ./Requisitos.pdf
* Requirements list created based on Requisitos.pdf: ./mapped_requirements.csv
* Repeatable Requirements Coverage Report: ./reqcov_report/coverage_report.html


## 1. Project Overview
* **Application under test:** SpringEspresso
* **Objective:** Ensure the quality of all the features through automated E2E web tests.
* **Technologies:** Playwright (JavaScript) and Node.js.

## 2. Prerequisites
Before starting, make sure you have installed:
* Visual Studio Code;
* Playwright extension for VS Code (optional, but recommended);
* [Node.js](https://nodejs.org/) (v16 or higher);
* Package manager (npm or yarn).

## 3. After cloning the project
In the command line,
* Run `npm install` to install node_modules;
* Run `npm install playwright@latest`to install Playwright;
* Run `npm install mysql2` to install the DB package necessary to clean the database after the tests.

## 4. Execution and Reports
In the command line,
* Run all tests using only the command line: `npx playwright test`;
* Run all tests using the UI mode to debug and explore the tests: `npx playwright test --ui`;
* Run all tests using Trace Viewer: `npx playwright test --trace on`;
* See the report from the previous run: `npx playwright show-report`;
* Create more tests using Playwright Codegen: `npx playwright codegen`.

PS. The tests were performed locally. Before testing, it is essential to download the project SpringEspresso and run it locally.


# SpringEspresso: a website to assist game testers

## Project Overview:
**Back end:**
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf

**Front end:**
- Javascript
- HTML
- CSS

**Database:**
- MySQL 

## Prerequisites
Before starting, make sure you have installed:
* Java JDK 11;
* Apache Maven;
* MySQL Server;
* IDE (Eclipse, IntelliJ or Visual Studio Code)

## Execution
1. Download or clone the project;
2. Access the project folder;
3. In the Command Line Terminal, run the command **mvn clean install**;
4. To run the project, run the command **mvn spring-boot:run**;
5. The application will run on **http://localhost:8080**;
6. The database will be created in the first execution.

