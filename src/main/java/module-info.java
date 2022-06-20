module audit4j.spring {
    requires aspectjrt;

    requires audit4j.core;

    requires javax.servlet.api;

    requires spring.aop;
    requires spring.beans;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.web;

    exports org.audit4j.integration.spring;
}
