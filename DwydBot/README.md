# DwydBot
A bot is an implementation of communication between a server and a client via API.

### Functional
Interaction with bots is implemented through buttons:
1. Помощь - when clicked, an image with instructions on how to add a product will be sent.
2. Добавить товар - when clicked, text instructions will be sent, after which the product must be added. 
The system will wait for the customer to enter the product. 
If the product is successfully added, the message "Товар добавлен" will be sent; otherwise, "Товар не добавлен."

### Setting
Basic settings for the bot and customization of text for buttons are carried out through files:
1. bot.properties - includes message text, button text, and a link for sending a request.
2. .env - contains basic bot settings, such as the token, bot name, and admin ID.

### Local start
If you want to start this project locally, after clone you need enter this command:
```groovy
gradle build
gradle run
```

### Stack
- Java 21;
- Gradle 8.5
- Telegrambots-client 9.0.0