### General Questions

#### What is the license?

fabric8 uses the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.txt).

#### What is it?

fabric8 is an integration platform based on Apache ActiveMQ, Apache Camel, Apache CXF, Hawtio and others.

It provides automated configuration and deployment management to help make deployments easy, reproducible, and less human-error prone.

Take a look [at this blog post](http://www.christianposta.com/blog/?p=376) to see a more detailed treatment.

#### What does fabric8 do?

fabric8 (pronounced _fabricate_) lets you create and manage fabrics (or clusters) of applications, integrations and middleware.

Try reading the [overview](/gitbook/overview.html) to see if that helps give you an idea what fabric8 is.

#### What Java versions is supported?

fabric8 runs on Java 7 and 8. 

#### Is Windows supported

We recommend using a linux based system for production; preferably if you want a fully managed platform use [Docker](http://docker.io/) and [Kubernetes](http://kubernetes.io) or [OpenShift Origin V3](https://github.com/openshift/origin).

Windows is currently only partially supported. Windows users may consider using [Docker](http://docker.io/) so that all the fabric8 technologies run inside a linux VM in lightweight containers.

#### Deprecations

FAB (Fuse Application Bundles) has been deprecated for the 1.2 release and removed form 2.x.
