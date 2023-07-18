# Makefile for JavaFX project

# Change the path to your javafx installation
# Guide: https://openjfx.io/openjfx-docs/#install-javafx
JAVAFX_LIB = /opt/javafx-sdk-20.0.1/lib

# Compiler and flags
JAVAC = javac
JAVAC_FLAGS = -d bin -sourcepath src --module-path $(JAVAFX_LIB) --add-modules javafx.controls,javafx.fxml

# Executable
MAIN_CLASS = GUI
SERVER_CLASS = Server



# Source and build directories
SRC_DIR = src
BUILD_DIR = bin

# Source files
SOURCES := $(shell find $(SRC_DIR) -name '*.java')

# Build target
TARGET = $(BUILD_DIR)/$(MAIN_CLASS).class

.PHONY: all clean run

all: $(TARGET)

$(TARGET): $(SOURCES)
	@mkdir -p $(BUILD_DIR)
	$(JAVAC) $(JAVAC_FLAGS) $(SOURCES)

clean:
	rm -rf $(BUILD_DIR)

start_server:
	@echo "Starting the server"
	java --module-path $(JAVAFX_LIB) --add-modules javafx.controls,javafx.fxml -cp $(BUILD_DIR) $(SERVER_CLASS)


run:
	@echo "Make sure the server is running, otherwise this will crash"
	java --module-path $(JAVAFX_LIB) --add-modules javafx.controls,javafx.fxml -cp $(BUILD_DIR) $(MAIN_CLASS)