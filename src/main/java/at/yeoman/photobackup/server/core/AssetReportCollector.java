package at.yeoman.photobackup.server.core;

import at.yeoman.photobackup.server.Directories;
import at.yeoman.photobackup.server.api.AssetReport;
import at.yeoman.photobackup.server.assets.AssetDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AssetReportCollector {
    private static final String Suffix = ".json";

    private static final Logger logger = LoggerFactory.getLogger(AssetReportCollector.class);

    private ObjectReader reader = new ObjectMapper().readerFor(AssetReport.class);
    private List<AssetDescription> assets = new ArrayList<>();
    private Set<AssetDescription> knownAssets = new HashSet<>();

    public final Assets result;

    AssetReportCollector() {
        collectAssetReports();
        result = new Assets(assets);
    }

    private void collectAssetReports() {
        File[] files = Directories.Assets.listFiles();
        List<Report> reports = reportsForFiles(files);
        reports.sort(Comparator.comparing(report -> report.creation));
        reports.forEach(this::integrateReport);
    }

    private List<Report> reportsForFiles(File[] files) {
         return Arrays
                .stream(Objects.requireNonNull(files))
                .flatMap(this::createReport)
                .collect(Collectors.toList());
    }

    private Stream<Report> createReport(File file) {
        if (!file.getName().endsWith(Suffix)) {
            return Stream.empty();
        } else {
            return createReportStream(file);
        }
    }

    private Stream<Report> createReportStream(File file) {
        String rawDate = rawDateForFileName(file.getName());
        try {
            return createReportStreamOrThrow(file, rawDate);
        } catch(DateTimeParseException exception) {
            logger.error("Unable to read raw instant from name of file [" + file.getAbsolutePath() + "]");
            return Stream.empty();
        }
    }

    private Stream<Report> createReportStreamOrThrow(File file, String rawDate) {
        Report result = new Report();
        result.file = file;
        result.creation = Instant.parse(rawDate);
        return Stream.of(result);
    }

    private class Report {
        File file;
        Instant creation;
    }

    private String rawDateForFileName(String name) {
        return name.substring(0, name.length() - Suffix.length());
    }

    private void integrateReport(Report rawReport) {
        try {
            AssetReport parsedReport = reader.readValue(rawReport.file);
            integrateReport(parsedReport);
        } catch (IOException e) {
            logger.error("Unable to read asset report from file [" + rawReport.file.getAbsolutePath() + "]");
        }
    }

    private void integrateReport(AssetReport report) {
        List<AssetDescription> filtered = report.getDescriptions().stream()
                .filter(asset -> !knownAssets.contains(asset))
                .collect(Collectors.toList());
        assets.addAll(filtered);
        knownAssets.addAll(filtered);
    }
}
