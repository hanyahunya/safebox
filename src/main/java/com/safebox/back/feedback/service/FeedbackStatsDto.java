package com.safebox.back.feedback.service;

public class FeedbackStatsDto {
    private long totalCount;
    private long pendingCount;
    private long inProgressCount;
    private long resolvedCount;
    private long closedCount;
    private long todayCount;
    private long bugCount;
    private long suggestionCount;
    private long complaintCount;
    private long complimentCount;

    // 기본 생성자
    public FeedbackStatsDto() {}

    // Getters and Setters
    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public long getPendingCount() { return pendingCount; }
    public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }

    public long getInProgressCount() { return inProgressCount; }
    public void setInProgressCount(long inProgressCount) { this.inProgressCount = inProgressCount; }

    public long getResolvedCount() { return resolvedCount; }
    public void setResolvedCount(long resolvedCount) { this.resolvedCount = resolvedCount; }

    public long getClosedCount() { return closedCount; }
    public void setClosedCount(long closedCount) { this.closedCount = closedCount; }

    public long getTodayCount() { return todayCount; }
    public void setTodayCount(long todayCount) { this.todayCount = todayCount; }

    public long getBugCount() { return bugCount; }
    public void setBugCount(long bugCount) { this.bugCount = bugCount; }

    public long getSuggestionCount() { return suggestionCount; }
    public void setSuggestionCount(long suggestionCount) { this.suggestionCount = suggestionCount; }

    public long getComplaintCount() { return complaintCount; }
    public void setComplaintCount(long complaintCount) { this.complaintCount = complaintCount; }

    public long getComplimentCount() { return complimentCount; }
    public void setComplimentCount(long complimentCount) { this.complimentCount = complimentCount; }
}