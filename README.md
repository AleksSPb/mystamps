# My Stamps

[![Build Status](https://api.travis-ci.org/php-coder/mystamps.png?branch=master)](https://travis-ci.org/php-coder/mystamps)
[![Coverage Status](https://coveralls.io/repos/php-coder/mystamps/badge.png)](https://coveralls.io/r/php-coder/mystamps)
[![Stories in Ready](https://badge.waffle.io/php-coder/mystamps.png?label=ready)](https://waffle.io/php-coder/mystamps)

## What's it?

It's a website for anybody who collects post stamps and wants to have online version of collection.

## How it can be useful for me?

With this site you can:
* see the statistic (including charts) about your collection (how many series and stamps do you have? From which countries and in which categories they are?)
* share link to collection with friends
* use it as list of stamps which you are selling on auction
* add to signature on forums
* use it where photo of your collection is needed

## How I can try it?

Site is under development and doesn't exist on the Internet so unfortunately you can't try it right now. You can watch on issue [#71](https://github.com/php-coder/mystamps/issues/71) and when it will be done, be a first beta users!

If you are programmer/sysadmin or you just feeling that you are able to run local version of the site then follow the following instructions:

* install Java (at least 7th version is required)
* install Maven
* clone this project
* from the console inside the directory with source code, execute command `mvn clean jetty:run`
* open up `http://127.0.0.1:8081` in the browser
* browse the site or log in as one of the pre-created users: `admin` / `test` or `coder` / `test`
* press `Ctrl-C` to stop the server

**Caution!** The purpose of that version is preview of the site and its capabilities. Because of that, the **changes** that you can make on the site **will be lost after stopping the server**!

## What's inside? (interesting only for programmers)

* *At the heart of*: Spring Framework
* *Template engine*: Thymeleaf
* *UI*: HTML, Bootstrap and a bit of JavaScript with JQuery
* *Security*: Spring Security
* *Databases*: H2 or MySQL
* *Database access*: Spring Data JPA (and Hibernate as JPA provider) or Spring's `JdbcTemplate`
* *Database migrations*: Liquibase
* *Validation*: JSR-303 (Hibernate Validator)
* *Logging*: Slf4j (log4j)
* *Unit tests*: Groovy and Spock Framework (for Java code), jasmine (for JavaScript code)
* *Integration tests*: Selenium2, TestNG and fest-assert
* *Other*: Lombok, Togglz, WebJars
