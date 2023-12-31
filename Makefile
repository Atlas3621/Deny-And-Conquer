# Makefile for JavaFX project

# Change the path to your javafx installation
# Guide: https://openjfx.io/openjfx-docs/#install-javafx
JAVAFX_LIB = /Users/nialls/Downloads/javafx-sdk-20.0.2/lib

# Compiler and flags
JAVAC = javac
JAVAC_FLAGS = -d bin -sourcepath src
JAVAFX_FLAGS = --module-path $(JAVAFX_LIB) --add-modules javafx.swing,javafx.graphics,javafx.fxml,javafx.media,javafx.controls

# Executable
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

run app: all
	java $(JAVAFX_FLAGS) -cp $(BUILD_DIR) $(APP)
