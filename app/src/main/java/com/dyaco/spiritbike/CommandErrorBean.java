package com.dyaco.spiritbike;

import com.corestar.libs.device.Device;

public class CommandErrorBean {

    private int errorType; //0 connect error, 1 model error, 2 command error
    private Device.COMMAND command;
    private Device.COMMAND_ERROR commandError;
    private String errorMessage;

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }

    public Device.COMMAND getCommand() {
        return command;
    }

    public void setCommand(Device.COMMAND command) {
        this.command = command;
    }

    public Device.COMMAND_ERROR getCommandError() {
        return commandError;
    }

    public void setCommandError(Device.COMMAND_ERROR commandError) {
        this.commandError = commandError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "CommandErrorBean{" +
                "errorType=" + errorType +
                ", command=" + command +
                ", commandError=" + commandError +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
