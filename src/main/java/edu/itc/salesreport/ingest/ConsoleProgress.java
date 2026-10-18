package edu.itc.salesreport.ingest;

public final class ConsoleProgress
        implements LoadListener {

    @Override
    public void onEvent(LoadEvent event) {
        switch (event) {
            case LoadEvent.FileLoaded e ->
                    System.out.printf(
                            "[%d/%d] %s: accepted %d, rejected %d%n",
                            e.filesDone(),
                            e.filesTotal(),
                            e.file().getFileName(),
                            e.accepted(),
                            e.rejected());

            case LoadEvent.LoadFinished e ->
                    System.out.printf(
                            "Finished: accepted %d, rejected %d, "
                                    + "time %d ms%n",
                            e.accepted(),
                            e.rejected(),
                            e.took().toMillis());
        }
    }
}
