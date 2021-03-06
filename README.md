# SpringInActionRevised
IMPORTANT NOTICE: This is not the offical code from the Spring in Action book. This is just my take on the code. I believe the github from the author is this one:
https://github.com/habuma/spring-in-action-5-samples

This project is mainly the code from Craig Wall's great book Spring in Action (https://www.manning.com/books/spring-in-action-fifth-edition) with some minor improvements
As I am working through this book I encountered some issues in some chapters that didn't work properly in 2020. The code here might help some of you who are working with this book and might get stuck. Additionally I tried to add some improvements, like improving the taco cloud here and there.
If you wonder about how the code works and what all these annotations mean, go get and read Spring in Action.

Chapter 1-2: Chapter 1-2 are part of the Chapter 03 folder. I haven't created them in separate directories. The sources of Chapter 1-2 are mostly as they appear in the book.


Chapter 3 Spring Data: This chapter added Spring Data to the mix. The version in this repository only has the JPA version and not the JDBC version as I was too lazy to add all the CrudRepository methods to the 
JDBCRepository implementations. It also helps to not having to use Qualifiers caused by having multiple Repository implementations. I fixed some issues that the tacos.Ingredient list valuation didn't work properly. I have also added an application
property so that the access to the h2-console is easier and you don't have to add the generated identifier to the JDBC URL.


Chapter 4 Spring Security: This chapter added Spring security. Some improvements I made in this chapter, were improving the UI. Making sure that the h2-console is accessible without need to authenticate, added more pages (error page, order complete) page, making the order form autofill the user data,
fixed the StandardPasswordEncoder deprecation and last but not least added default user to the data.sql. You can now login with Woody/bullseye and Buzz/infinity into the tacos.Taco Cloud app. Unfortunately I had to disable csrf protection to
make it work. With it enabled the order page threw a 403 even with the hidden input included. This could be improved.

Chapter 4 Update: Added a few more unit tests to test the different controllers. There could be more, but as this is just an app to learn about Spring, this is enough. Note: I couldn't figure out how to use the AuthenticationPrincipal
in tests. No StackOverflow answer helped in my case.

Chapter 4 Update 2: Finally solved the AuthenticationPrincipal issue by configuring the MockMvcs with springSecurity() and using the withUser(UserDetails) method. Tests are now improved.

Chapter 5 Spring properties: Added a new page to see the last orders, that use the new property taco.orders.pageSize property. Added test for that. Also added new functionality to actually add multiple tacos to same order. UX Design
can be improved, but this is not an app about learning fronted, but about Spring.

Chapter 6: Spring Rest application. That was a bit of work to split up the project into separate components. But it works. Unfortunately using REST doesn't work so well with the old UI, so it had to be redone to Angular. 
This also breaks the unit tests, as they were testing the old ui. The Angular code is just a copy from the original author, as I will need to work on my Angular knowledge. As I do not want to get sidetracked from learning Spring I will postpone it.

Chapter 7: Will be about consuming the REST Api, quite important I would say.
