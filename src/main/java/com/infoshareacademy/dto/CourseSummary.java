package com.infoshareacademy.dto;

public class CourseSummary {

    private int attendees;
    private String name;

    public CourseSummary(int attendees, String name) {
        this.attendees = attendees;
        this.name = name;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("CourseSummary{");
        sb.append("attendees=").append(attendees);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
