# LoadBalancedCommunications
Project for Computer Networks course.





(1)Load Balancing Strategy

I followed a strategy similar to the Least Connection strategy.
When a client sends a request my load balancer is searching for the server which has least
active connections. My servers supports at most 100 request. I used a double array to track
which server has what amount of work. To test purposes i created 4 server threads. When a
client sends a request a function starts and searches this double array. In this array "1" 
indicates a work. The fuction compares these works(not weighted) and finds the emptiest
server and sends its port number to the client. Then client uses that port and sends its
request to that server. When a server finish the request it sends an message which indicates
that the work is done and load balancer reads this message and erases a "1" from that server's
double array. If all servers are full load balancer sends a message to the client about the
situation and it is not taking any other request until a place opens.

(2)High Level Approach

I used UDP protocol to imlement this project.
Servers answers to three different requests. First one is directory listing, to make the 
project easily runnable in every computer it lists the project's files. Second one is file
transfer. It sends the "deneme.txt" file which is in the project's directory to the client.
Client downloads this file with a different name to the project's directoy. The reason is 
the same with the first one to make it easier for you to test. Third one is a computation 
as requested. It makes the thread sleep for 40 seconds. And every 10 second it sends an 
update to notify the client about remaining time. Any other choice or message send by the
client closes every server.

(3)Challanges

Since this project is not limits us with a specific load balancing strategy, i think
finding an idea about how to do that load balancing algorithm is easy. I mean i could have
just assign servers randomly.Instead of that i tried to do something optimal enough, but not 
above my capabilities. First i tried to do something preemtive like when a server was doing
computation if another client wants a fast request server would do that fast request and then
will continue to the computation request. But i couldn't figure out how to continue to do
the previous request. Then i think about something like shortest job first(SJF), but this
method could lead to a starvation of a request. Finally i came out with this idea of  least 
connection strategy as i said i think it is simpler then the most strategies but not the 
worst idea.


(4)Testing

Since this is not a very big project i did my tests manually, after i write some 
code i did unit testing. Since this project covers a different threads and clients the 
important part was how these units integrates with the other ones for that reason i tried to
do integration testings very carefully. And i met most of the errors in this part. I did 
debugging to understand the problems and i solved every one of them. I think at the end there
is a precaution or a limitation for every possible error.


(5)How to run project

As i mentioned in the  second part(High Level Approach) i tried to do the client's 
options to work on different directories or computers.

1) Load Balancer's port number is 9876 if this port is used in your computer, you 
need to change it manually. Other ports selected by the system.
2) There is a file "downloadedFile.txt" in project's directory, you can delete that
to test that the option 2 of client is working.
3) First you need to run loadBalancer.java
4) Then you need to run Server.java it has 4 server threads an when it runs they
are joining the load balancer so the order is important.
5) Then you can run as many Client.java as you want. And through feedbacks you can
track whats happening. Since other requests is fast best way to test is using option 3(computation)



NOTE:  I used JDK 15 and Apache NewBeans IDE 12.4
