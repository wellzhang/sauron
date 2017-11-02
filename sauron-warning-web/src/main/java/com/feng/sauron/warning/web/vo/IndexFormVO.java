package com.feng.sauron.warning.web.vo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by lianbin.wang on 2016/11/16.
 */
public class IndexFormVO {
    private String appName;
    private String host;
    private String timeBefore;
    private String timeSpan;


    //散点图联动
    private String type;
    private String status;
    private long start;
    private long end;

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTimeBefore() {
        return timeBefore;
    }

    public void setTimeBefore(String timeBefore) {
        this.timeBefore = timeBefore;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public static class ParsedFormVO {
        private Date startTime;
        private Date endTime;
        private String appName;
        private String host;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }
    }


    public ParsedFormVO parse() {
        ParsedFormVO vo = new ParsedFormVO();
        vo.setAppName(this.appName);
        vo.setHost(this.host);

        if (StringUtils.isNotBlank(this.timeSpan)) {
            String[] split = this.timeSpan.split(" - ");
            if (split.length != 2) {
                throw new IllegalArgumentException("时间范围不合法");
            }

            try {
                Date startTime = DateUtils.parseDate(split[0], "yyyy-MM-dd HH:mm:ss");
                Date endTime = DateUtils.parseDate(split[1], "yyyy-MM-dd HH:mm:ss");

                vo.setStartTime(startTime);
                vo.setEndTime(endTime);
            } catch (ParseException e) {
                throw new IllegalArgumentException("时间格式不合法", e);
            }

        } else if (StringUtils.isNotBlank(this.timeBefore)) {
            Date now = new Date();
            vo.setEndTime(now);

            Date startTime = TimeBefore.showOf(this.timeBefore).parse(now);
            vo.setStartTime(startTime);
        } else if (this.start > 0 || this.end > 0) {
            vo.setStartTime(new Date(this.start));
            vo.setEndTime(new Date(this.end));
        } else {
            throw new IllegalArgumentException("时间参数不合法");
        }

        return vo;
    }

    enum TimeBefore {
        Min5("5min") {
            @Override
            Date parse(Date now) {
                return new Date(now.getTime() - 5 * 1000 * 60);
            }
        },
        Min20("20min") {
            @Override
            Date parse(Date now) {
                return new Date(now.getTime() - 20 * 1000 * 60);
            }
        },
        Hour1("1h") {
            @Override
            Date parse(Date now) {
                return new Date(now.getTime() - 60 * 1000 * 60);
            }
        },
        Hour3("3h") {
            @Override
            Date parse(Date now) {
                return new Date(now.getTime() - 3 * 60 * 1000 * 60);
            }
        };

        private String show;

        TimeBefore(String show) {
            this.show = show;
        }

        abstract Date parse(Date now);

        public static TimeBefore showOf(String timeStr) {
            TimeBefore[] values = values();
            for (TimeBefore value : values) {
                if (value.show.equalsIgnoreCase(timeStr)) {
                    return value;
                }
            }
            throw new IllegalStateException("no enum of: " + timeStr);
        }

    }
}
