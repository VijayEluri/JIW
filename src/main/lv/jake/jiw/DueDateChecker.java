package lv.jake.jiw;

import com.google.inject.Inject;
import lv.jake.jiw.application.TimeService;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DueDateChecker {
    private static org.apache.log4j.Logger log = Logger.getLogger(DueDateChecker.class);
    public static final String NOT_VALID = "not valid";
    public static final String OVERDUE = "overdue";
    public static final String SLA_OVERDUE = "sla overdue";
    public static final String NOT_COMMENTED = "not commented";
    public static final String OK = "ok";
    public static final String DUE_DATE_SOON = "due date soon";
    public static final String SLA_SOON = "sla soon";
    public static final String DUE_DATE_NOT_SET = "due date is not set";

    protected final TimeService timeService;

    @Inject
    public DueDateChecker(TimeService timeService) {
        this.timeService = timeService;
    }

    public String getDueDateStatus(Date created, Date duedate, String priority, Date updated) {
        Calendar createdDateCalendar = createCalendarFromDate(created);
        Calendar updatedDateCalendar = createCalendarFromDate(updated);
        Calendar dueDateCalendar = null;
        if (duedate != null) {
            dueDateCalendar = createCalendarFromDate(duedate);
            dueDateCalendar.set(Calendar.HOUR, 18);
            dueDateCalendar.set(Calendar.MINUTE, 0);
        }

        if (Integer.valueOf(priority) == 1) {
            return getStatusForBlocker(dueDateCalendar, updatedDateCalendar, createdDateCalendar);
        }
        if (Integer.valueOf(priority) == 2) {
            return getStatusForCritical(dueDateCalendar, updatedDateCalendar, createdDateCalendar);
        }
        if (Integer.valueOf(priority) == 3) {
            return getStatusForMajor(dueDateCalendar, updatedDateCalendar, createdDateCalendar);
        }
        if (Integer.valueOf(priority) == 4) {
            return getStatusForMinor(dueDateCalendar, updatedDateCalendar, createdDateCalendar);
        }
        if (Integer.valueOf(priority) == 5) {
            return getStatusForTrivial(dueDateCalendar, updatedDateCalendar, createdDateCalendar);
        }
        return NOT_VALID;
    }

    private Calendar createCalendarFromDate(Date created) {
        Calendar createdDateCalendar;
        createdDateCalendar = GregorianCalendar.getInstance();
        createdDateCalendar.setTime(created);
        return createdDateCalendar;
    }

    public String getStatusForBlocker(Calendar duedate, Calendar updated, Calendar created) {
        Calendar currentDate = timeService.getCalendar();

        if (duedate == null && getTimeDifferenceInMinutes(created, currentDate) > 10) {
            return DUE_DATE_NOT_SET;
        }

        if (getTimeDifferenceInMinutes(updated, currentDate) > 58) {
            return NOT_COMMENTED;
        }

        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) < 24 && getTimeDifferenceInHours(currentDate, duedate) > 0) {
            return DUE_DATE_SOON;
        }

        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) <= 0) {
            return OVERDUE;
        }

        if (getTimeDifferenceInHours(created, currentDate) > 4) {
            return SLA_OVERDUE;
        }
        return OK;
    }

    public String getStatusForCritical(Calendar duedate, Calendar updated, Calendar created) {
        Calendar currentDate = timeService.getCalendar();

        if (duedate == null && getTimeDifferenceInMinutes(updated, currentDate) > 30) {
            return DUE_DATE_NOT_SET;
        }

        if (getTimeDifferenceInHours(updated, currentDate) > 4) {
            return NOT_COMMENTED;
        }

        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) < 24 && getTimeDifferenceInHours(currentDate, duedate) > 0) {
            return DUE_DATE_SOON;
        }

        if (duedate != null && getTimeDifferenceInMinutes(currentDate, duedate) <= 0) {
            return OVERDUE;
        }

        if (getTimeDifferenceInDays(created, currentDate) > 2) {
            return SLA_OVERDUE;
        }
        return OK;
    }

    public String getStatusForMajor(Calendar duedate, Calendar updated, Calendar created) {
        Calendar currentDate = timeService.getCalendar();
        if (duedate == null && getTimeDifferenceInHours(created, currentDate) > 24) {
            return DUE_DATE_NOT_SET;
        }
        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) < 24 && getTimeDifferenceInHours(currentDate, duedate) > 0) {
            return DUE_DATE_SOON;
        }
        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) <= 0) {
            return OVERDUE;
        }
        return OK;
    }

    public String getStatusForMinor(Calendar duedate, Calendar updated, Calendar created) {
        Calendar currentDate = timeService.getCalendar();
        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) < 24 && getTimeDifferenceInHours(currentDate, duedate) > 0) {
            return DUE_DATE_SOON;
        }
        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) <= 0) {
            return OVERDUE;
        }
        return OK;
    }

    public String getStatusForTrivial(Calendar duedate, Calendar updated, Calendar created) {
        Calendar currentDate = timeService.getCalendar();
        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) < 24 && getTimeDifferenceInHours(currentDate, duedate) > 0) {
            return DUE_DATE_SOON;
        }
        if (duedate != null && getTimeDifferenceInHours(currentDate, duedate) <= 0) {
            return OVERDUE;
        }
        return OK;
    }

    public long getTimeDifferenceInMinutes(Calendar startDate, Calendar endDate) {
        long milliseconds1 = startDate.getTimeInMillis();
        long milliseconds2 = endDate.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        return diff / (60 * 1000);
    }

    public long getTimeDifferenceInHours(Calendar startDate, Calendar endDate) {
        long milliseconds1 = startDate.getTimeInMillis();
        long milliseconds2 = endDate.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        return diff / (60 * 60 * 1000);
    }

    public long getTimeDifferenceInDays(Calendar startDate, Calendar endDate) {
        long milliseconds1 = startDate.getTimeInMillis();
        long milliseconds2 = endDate.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        return diff / (24 * 60 * 60 * 1000);
    }
}
