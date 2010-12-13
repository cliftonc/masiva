grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		//build 'com.amazonaws:aws-java-sdk:jar:1.1.1'		
		//build 'org.apache.solr:solr-solrj:1.4.0'
		build 'commons-codec:commons-codec:1.3'
		build 'commons-httpclient:commons-httpclient:3.1'
		build 'commons-io:commons-io:1.4'
		build 'org.apache.geronimo.specs:geronimo-stax-api_1.0_spec:1.0.1'
		build 'org.slf4j:jcl-over-slf4j:1.5.5'
		build 'org.codehaus.woodstox:wstx-asl:3.2.7' 
    }
}
