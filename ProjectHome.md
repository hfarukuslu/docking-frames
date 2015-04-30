

## What is this? ##

this is a staging location to migrate/publish this project to maven:

http://dock.javaforge.com/

## Where is RELEASE repo? ##

releases are published on maven central:

http://repo1.maven.org/maven2/

## Where is SNAPSHOT repo? ##

it is hosted by Sonatype:

http://oss.sonatype.org/content/repositories/snapshots/

## What is the latest version? ##

look here:

http://oss.sonatype.org/content/repositories/snapshots/org/dockingframes/

## How do I make it a maven dependency? ##

sample dependency declaration:
```
  <dependencies>
        <dependency>
                <groupId>org.dockingframes</groupId>
                <artifactId>docking-frames-common</artifactId>
                <version>1.0.8-SNAPSHOT</version>
                <type>jar</type>
                <scope>compile</scope>
        </dependency>
  </dependencies>
```

## Can you show me how to use it as a SNAPSHOT dependency? ##

here is a sample project whith docking-frames as maven snapshot dependency:

http://code.google.com/p/docking-frames/source/browse/#svn/test/trunk/docking-frames-test-maven

## How frequently SNAPSHOT versions are published? ##

after any changes is detected in the original project repo;

## How frequently RELEASE versions are published? ##

after Beni issues a blessing to do so;

## Some Examples ##

### How to rotate flap station title ###

http://code.google.com/p/docking-frames/source/browse/#svn/demo/trunk/docking-frames-examples/src/main/java/org/dockingframes/example/flap_title_direction

### How to mimic widgetfx.org dock ###

http://code.google.com/p/widgetfx/

http://code.google.com/p/docking-frames/source/browse/#svn/demo/trunk/docking-frames-examples/src/main/java/org/dockingframes/example/fixed_screen_dock

### How to show drag/drop outline for a theme ###

http://code.google.com/p/docking-frames/source/browse/#svn/demo/trunk/docking-frames-examples/src/main/java/org/dockingframes/example/eclipse_theme

