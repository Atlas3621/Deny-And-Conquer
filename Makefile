# Makefile for JavaFX project

# Change the path to your javafx installation
# Guide: https://openjfx.io/openjfx-docs/#install-javafx
JAVAFX_LIB = /opt/javafx-sdk-20.0.1/lib

# Compiler and flags
JAVAC = javac
JAVAC_FLAGS = -d bin -sourcepath src
JAVAFX_FLAGS = --module-path $(JAVAFX_LIB) --add-modules javafx.controls,javafx.fxml

# Executable
GUI1 = GUI
ClientGUI = ClientGUI
SERVER_CLASS = Server
APP = App

# Source and build directories
SRC_DIR = src
BUILD_DIR = bin

# Source files
SOURCES := $(shell find $(SRC_DIR) -name '*.java')

# Build target
TARGET = $(BUILD_DIR)/$(GUI1).class

.PHONY: all clean run

all: $(TARGET)

$(TARGET): $(SOURCES)
	@mkdir -p $(BUILD_DIR)
	$(JAVAC) $(JAVAC_FLAGS) $(JAVAFX_FLAGS) $(SOURCES)

clean:
	rm -rf $(BUILD_DIR)

start_server: all
	@echo "Starting the server"
	java $(JAVAFX_FLAGS) -cp $(BUILD_DIR) $(SERVER_CLASS)


run gui1: all
	@echo "Make sure the server is running, otherwise this will crash"
	java $(JAVAFX_FLAGS) -cp $(BUILD_DIR) $(GUI1)
run gui2: all
	@echo "Make sure the server is running, otherwise this will crash"
	java $(JAVAFX_FLAGS) -cp $(BUILD_DIR) $(ClientGUI)

run app: all
	@echo "Make sure the server is running, otherwise this will crash"
	java $(JAVAFX_FLAGS) -cp $(BUILD_DIR) $(APP)
