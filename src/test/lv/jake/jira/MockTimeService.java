package lv.jake.jira;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
* Author: Konstantin Zmanovsky
* Date: Apr 12, 2010
* Time: 8:27:10 PM
*/
class MockTimeService implements TimeService {
    public Calendar getCalendar() {
        return new GregorianCalendar(2010, 1, 15);
    }
}