# Amazon Price Scraper Specific Requirements

## Introduction
___
A major issue many individuals face while online shopping is finding a good price for the item they wish to purchase. Most just purchase the first search result that appears on Amazon or eBay, only to find out that they paid more than what the item is actually worth. This is where the price management software comes into play. This provides users with an easy-to-use application that generates a list of the best matching search results with the best prices, as well as all time lows and highs for the item searched. This will help resolve the struggle that is online shopping for many. The application will come with a variety of useful features such as localized search data, and detailed statistics.

### Purpose
This document outlines the features that the price management segment will provide to users.

### Scope
As a generic price management module, this application seeks to streamline the online shopping experience. Thus, this module is generic to all online shoppers and is not exclusive to any sub-group. To start, the application will be limited to Canadian listings.

### Document Lexicon
Definitions:
- **Scraper:** Data scraping is a technique in which a computer program extracts data from human-readable output coming from another program.

Acronyms and Abbreviations:
- **API - Application Programming Interface:** Tools for building application software.

### References
Based on [IEEE Std 803-1998.](https://standards.ieee.org/findstds/standard/830-1998.html)

## Overall Description
___
**Searching with app:** Users who wish to find the lowest price for an item of their choice may do so at any time without charge or creation of account. Users have complete access to the data provided by the application and search results may be sorted with customized search filters designed with users in mind. Standard search fields will include:
- **Price** – price of the searched item
- **Item Rating** – rating of searched item

Once a user finds the item of their liking, they have the ability to save this item to a watch list to track the price for the future as well as the ability to access the item page to proceed with purchase. All user lists are locally stored and device specific.

### Product Perspective

The application will attempt to use price scraping APIs as well as mapping API to help users localize search results and data.

**Actors and Use Cases**

Users:
- View saved items
- Search items
- Save items

Administrator:
- Maintain codebase

### Front-End Design
The front-end user interface will be simplistic in design to allow users of all computer literacy to navigate the application. The home page will consist of a search bar in the center of the page where users may input links to their desired item to generate data about the item. 

The main feature set available to front-end users:

**General User – the generic online shopper**
- These individuals do not have to register to use the application
- They may edit location and set various filters on their searches (i.e. they may sort by price)
- Once they find the item they are interested in, they may be redirected to the site to commit to purchase, or save the item to their watch list to monitor the price for the future 

### Additional Features
- **Pricing statistics:** Users may be provided with a detailed pricing history of the item they seek. This will be displayed on "press" of a specific item in the search results page or in their watch list. 

### Product Functions
Every individual will have complete access to all features provided on the application module. If a user has specific requirements that need to be met, then they may use the filters provided while searching for their item. In addition, a user may navigate directly to their item listing to proceed with their purchase.

### User Characteristics
This application module is made for the general public with basic knowledge of how to navigate an application. The module will be designed as straightforward as possible so even the most non-technologocial individuals can use it.

### Constraints
Google API only allows for 25000 free map loads per day. To make the app as cost-efficient as possible, if the max map loads is reached then no automatic location will be applied. Price scraping APIs allow limited requests and Amazon API is limited to only affiliates. It will be quite difficult to find a suitable price scraping API for cost-effectiveness. Upon creation, it is not expected that the module will receive many hits and thus limitations on API calls should not be met.

### Assumptions and Dependences
The assumption is made that users have access to an android device with android version 22 or higher as well as a stable internet connection.

## Specific Requirements
___

### External Interfaces

Unless otherwise stated, all inputs listed will be stored in the application local database as is appropriate for the model it represents.

- **Item Management**
    - The user will be allowed to modify their watch list by adding, or removing items.
- **Data Management**
    - The user will be presented with complete details about their searched item including but not limited to:
        - price
        - pictures
        - description
        - history

### Functions
- The system will perform basic validation for all models. For example, an invalid link will not provide any search results and prompt the user about the potential bad link.

### Logical Database Requirements
There will be one table with many attributes. The table will have a unique ID as a primary key.
- **Items Table:** The Items table will require a distinct item ID for each entry. The attributes will include the variety of item descriptors provided by the API used.

### Portability

We must create a functional application that is accessible any android device with android version 22 or higher. 

- Server-side: The use of a well-supported, platform agnostic language and framework such as Java is recommended. Clients do not need to support these technologies as Android OS will provide them.
- Client-side: Clients will need a device that supports Android OS 22 or higher.

### Performance Requirements
This application is intended as a proof-of-concept and not for production-level scalability. As such, while common sense is to be used when implementing features, pre-optimization is not desired. For example, the application should be able to support 50 user requests per minute. 

### Reliability
The application will not crash or produce unexpected errors when within the performance confinements outlined in "Performance Requirements". Additionally, the app will likely depend on a database server linked to the API and website the data comes from, so cases where this server becomes unavailable should be considered. As this is a proof of concept, load balancing, automated scaling, host health checks, or other reliability strategies are not required at this stage.

### Security
There are no security requirements for the application module since there is no sensitive data being processed.


