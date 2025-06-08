package com.example.shabashka;

import java.util.ArrayList;
import java.util.List;

public class FilterUtils {
    public static List<Job> filterJobs(List<Job> allJobs, String query) {
        List<Job> filtered = new ArrayList<>();
        query = query.toLowerCase().trim();

        for (Job job : allJobs) {
            if (job.getTitle().toLowerCase().contains(query)
                    || job.getLocation().toLowerCase().contains(query)) {
                filtered.add(job);
            }
        }
        return filtered;
    }
}
