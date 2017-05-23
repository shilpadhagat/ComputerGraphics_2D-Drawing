#
# Makefile for Java OpenGL based on LWJGL
#   rdb
#   08/10/2014 Created from JOGL version except this version does not
#              depend on the user's CLASSPATH already being set to include
#              the LWJGL jar files
#   08/18/2014 
#   09/06/2014 Changes to provide alternate locations for libraries and
#              the Main class name with environment variable override 
#              for grading.
#
# Labeled entries:
#     all:    build + run
#     build:  compile all java files
#     run:    run the java class designated as MAIN
#     clean:  delete all class files, build, run
#     clear:  delete all class files
#
# The default MAIN variable is the name of the directory. Thus, if the
#   main class name (which should start with a capital letter is the name
#   of the directory (starting with a capital letter), do nothing.
# Else simply modify the MAIN variable to specify the name of the main
#   class to be executed
#
#---------------------------------------------------------------------

# Get main class from directory name: or change to whatever name you want.
#   BUT use ?=. When running your Makefile, we can override whatever you
#   use with our own names in order to fit our environment.
#-----------------------------
#you can replace the expression after ?= to any name you want
MAIN ?= $(notdir ${PWD} )
#MAIN ?= AppName
#---------------------------

# If LWJGL is set in the environment, use it, 
# else set it to the cs770 home if it exists (for running on cs workstations)
# else set it to your location as defined by MYLWJGL, which defaults
#      to the cs770/lwjgl3 folder in your home directory
#
#--------------------------------
# Change MYLWJGL at your convenience, but don't touch anything else.
MYLWJGL=$(HOME)/cs770/lwjgl3
#--------------------------------

LWJGL ?=     #define LWJGL as null string if not already defined
ifeq ($(LWJGL),)    #if LWJGL is the null string, try to set it
    LWJGL = $(wildcard /home/csadmin/cs770/lwjgl3)
    ifeq ($(LWJGL),)
        LWJGL=$(MYLWJGL)
    endif
    ifeq ($(LWJGL),)
        $(error ERROR: can't find lwjgl home at $MYLWJGL)
    endif
endif

JARS = $(wildcard $(LWJGL)/jar/*jar) 
LIBDIR = $(LWJGL)/native

JVMFLAGS = 

#---------- System dependencies ---------------------------------------
# The lwjgl native libraries are in 
#    $LWJGL/native/{os-identifier}/...

OS := $(shell uname)

# since I don't know the OS designation for Windows, that's the default
nativelib := liblwjgl.dll
ifeq ($(OS),Linux)
	nativelib := liblwjgl.so
endif
ifeq ($(OS),Darwin)
	nativelib := liblwjgl.dylib
    JVMFLAGS +=  -XstartOnFirstThread
endif

LIBFLAGS = -Djava.library.path=$(LIBDIR)

#----------- get all jar files needed in the classpath ------------------
# The jar files are in the lwjgl installation directory in the "jar"
# directory. As delivered, lwjgl has 8 jars, but 2 define the same library:
#     lwjgl.jar
#     lwjgl-debug.jar
# You should remove 1 of these from your "lwjgl/jar" folder. 
#      I recommend that you KEEP lwjgl-debug.jar. 
#      It provides better feedback on errors.
#
# Next line gets a space separated list of jar files; we need it to be
#    be : -separated for the -cp switch.

# Next 2 lines get a space into "space"
space :=
space +=
# This line converts all spaces to :
JARS := $(subst $(space),:,$(JARS))

# proc:none turns off annotation processing. We use it to avoid a
#   warning message from compiling in the context of the gluegen 
#   annotation processor, which may be compiled for an older Java version.
JCFLAGS = -proc:none

# Now add in the class path and include .
#if want only want lwjgl classpath:
JCFLAGS += -cp .:$(JARS)
#if want existing classpath also: 
#JCFLAGS += -cp .:$(JARS):$(CLASSPATH)

# Library options

#---------- Application info ------------------------------------------

SRCS = $(wildcard *java)

# for every .java input, need to produce a .class
OBJS = $(SRCS:.java=.class) 

#------------------- dependencies/actions ------------------------
# dependency: need1 need2 ...  
#         action(s)
#
.PHONY: clean clear 

all:	build run

build: compile $(MAIN)

compile: $(OBJS) 

clean:	clear build run

%.class : %.java 
	javac $(JCFLAGS) $(SRCS)

$(MAIN): $(OBJS) $(COBJS)

run:
	java $(JVMFLAGS) $(LIBFLAGS) -cp $(JARS) $(MAIN)

clear:
	rm -f *.class 
