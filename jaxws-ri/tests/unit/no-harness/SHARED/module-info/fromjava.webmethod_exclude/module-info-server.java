module server {
    requires java.xml.ws;
    requires jdk.httpserver;
    requires java.logging;
    requires java.rmi;

    // generated by WebServiceWrapperGenerator
    exports fromjava.webmethod_exclude.jaxws;
    exports fromjava.webmethod_exclude;
}