This is a demo *Spring Boot*  ecommerce app using different technologies such as: *MySQL, Thymeleaf, Javascript* etc.



![tech stack](./notes/md-pics/tech.png)

​	The application has 2 modules. First one is the administrative module accessible by the users maintaining the app. The second one is the actual store accessible by all the users and clients. I used *Spring Security* in order to create several roles that will have access to some features in the administrative module. Also for clients the logging process can be done traditionally with user and password or by using their own *Facebook* or *Google* account (*Oauth2*).

​	 *Spring Data JPA* was used for the interaction with the MySQL database. The app can also send emails to clients whenever a client creates an account or needs to recover the password and also when a client makes a transaction in the store. As a payment solution I am using the *PayPal API*.

​	For the frontend I have chosen *Thymeleaf* (server-side) that uses *HTML, CSS and JavaScript*. Other libraries that are used: *Font Awesome* (images), *Google Charts* (graphical features - reports), *Super-CSV* (export CSV reports), *OoXML* (export XML reports), *OpenPDF*  (export PDF reports).

​	For testing I used a MySQL and a *Mailhog* (dummy mail server) Docker containers.

The database schema:

![db-schema](./notes/md-pics/db-schema.png)

​	**Screenshots for the administration module:**

![log in](./notes/md-pics/0.png)

![users](./notes/md-pics/1.png)

![user edit](./notes/md-pics/2.png)

![new user](./notes/md-pics/3.png)

![product categories](./notes/md-pics/5.png)

![brands](./notes/md-pics/6.png)

![products](./notes/md-pics/7.png)

![product details](./notes/md-pics/8.png)

![new product](./notes/md-pics/9.png)

![reviews](./notes/md-pics/10.png)

![clients](./notes/md-pics/11.png)

![delivery rates](./notes/md-pics/13.png)

![orders](./notes/md-pics/14.png)

![invoice export](./notes/md-pics/15.png)

![sales report](./notes/md-pics/17.png)

![pie for categories](./notes/md-pics/18.png)

![details for product sales](./notes/md-pics/19.png)

![settings](./notes/md-pics/20.png)

![email templates](./notes/md-pics/21.png)



**Screenshots for the store:**

![store frontpage](./notes/md-pics/22.png)

![search product](./notes/md-pics/23.png)

![product detail](./notes/md-pics/24.png)

![image carousel](./notes/md-pics/25.png)

![diverse security validations](./notes/md-pics/26.png)

![client registering using username+password](./notes/md-pics/27.png)

![client auth](./notes/md-pics/27a.png)

![using Oauth2](./notes/md-pics/27b.png)

![client account edit details](./notes/md-pics/27c.png)

![multiple addresses to choose from for delivery](./notes/md-pics/28.png)

![forgot password?](./notes/md-pics/29.png)

![reset password email](./notes/md-pics/30.png)

![save invoice](./notes/md-pics/32.png)

![client orders with different order state and allowed operations](./notes/md-pics/39.png)

![checkout](./notes/md-pics/40.png)

![email templates](./notes/md-pics/41.png)

![PayPal API](./notes/md-pics/41b.png)

![email for new order](./notes/md-pics/42.png)

![new product review](./notes/md-pics/43.png)

![review in product detail page](./notes/md-pics/44.png)

![responsivity of the app](./notes/md-pics/45.png)