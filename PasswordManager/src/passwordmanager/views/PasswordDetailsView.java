package passwordmanager.views;

import org.jline.terminal.Attributes;
import passwordmanager.data.BrowseTypes;
import passwordmanager.data.CommonData;
import passwordmanager.data.Hasher;

import java.io.IOException;
import java.time.LocalDateTime;

public class PasswordDetailsView extends AView {
    private final CommonData entry;
    private String feedbackMessage = "";
    private final BrowseTypes prevBrowseType;

    public PasswordDetailsView(CommonData entry, BrowseTypes prevBrowseType) {
        super(null);
        this.prevBrowseType = prevBrowseType;
        this.entry = entry;
    }

    @Override
    public AView showContent() {
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            while (true) {
                clearScreen();
                displayHeader();

                terminal.writer().println(centerText("=== ENTRY DETAILS ==="));
                terminal.writer().println();

                terminal.writer().println("   Service:  " + entry.getService());
                terminal.writer().println("   Username: " + entry.getUsername());
                terminal.writer().println("   Password: " + "********");
                terminal.writer().println("   Updated:  " + entry.getLastUpdatedOn());
                terminal.writer().println("   Notes:    " + (entry.getNotes() != null ? entry.getNotes() : "---"));

                terminal.writer().println("\n" + centerText(feedbackMessage));

                terminal.writer().println(separator);
                terminal.writer().println(centerText("[P] - Copy password | [ESC/ENTER] - Back to Browse"));
                terminal.writer().println(centerText("[U] - Update password | [D] - Delete password "));
                terminal.writer().flush();

                int code = terminal.reader().read();

                if (code == 'p' || code == 'P') {
                    handleCopyAction();
                } else if (code == 'u' || code == 'U') {
                    handleUpdateAction();
                } else if (code == 'd' || code == 'D') {
                    AView nextView = handleDeleteAction();
                    if (nextView != null) return nextView;
                    break;
                } else if (code == 27 || code == 13 || code == 10) {
                    return new BrowsePasswordsView(prevBrowseType);
                } else {
                    feedbackMessage = "";
                }
            }
        } catch (IOException e) {
            return new StartView("Error displaying details.");
        } finally {
            terminal.setAttributes(originalAttributes);
        }
        return new BrowsePasswordsView(prevBrowseType);
    }

    private void handleCopyAction() {
        String pass = entry.getPassword();
        if (pass == null) {
            feedbackMessage = "!!! Decryption failed: Wrong key or corrupted data !!!";
        } else {
            copyToClipboard(pass);
            feedbackMessage = ">>> Password copied to clipboard! <<<";
        }
    }

    private void handleUpdateAction() throws IOException {
        String newPassword = Hasher.generateStrongPassword();

        terminal.writer().println("\n" + centerText("!!! You're about to overwrite existing password !!! "));
        terminal.writer().println(centerText("Make sure you won't lose access to your account!"));
        terminal.writer().println(centerText("Press [Y] to confirm update and save, any other key to cancel"));

        terminal.writer().flush();

        int confirm = terminal.reader().read();
        if (confirm == 'y' || confirm == 'Y') {
            entry.setPassword(newPassword);
            entry.setLastUpdatedOn(LocalDateTime.now());
            writer.updateEntry(entry);
            feedbackMessage = ">>> Password updated successfully! <<<";
        } else {
            feedbackMessage = ">>> Update cancelled <<<";
        }
    }

    private AView handleDeleteAction() throws IOException {
        terminal.writer().println("\n" + centerText("!!! ARE YOU SURE? THIS CANNOT BE UNDONE !!!"));
        terminal.writer().println(centerText("Press [Y] to confirm delete, any other key to cancel"));
        terminal.writer().flush();

        int confirm = terminal.reader().read();
        if (confirm == 'y' || confirm == 'Y') {
            writer.deleteEntry(entry);
            return new BrowsePasswordsView(prevBrowseType);
        } else {
            feedbackMessage = ">>> Deletion cancelled <<<";
            return null;
        }
    }

    private void copyToClipboard(String text) {
        if (text == null || text.isEmpty()) return;

        boolean success = false;
        final String[] methodUsed = {""};

        try {
            java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(text);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            success = true;
            methodUsed[0] = "AWT";
        } catch (Throwable t) {
            try {
                if (runXclipCopy(text)) {
                    success = true;
                    methodUsed[0] = "XCLIP";
                }
            } catch (Exception e) {
                success = false;
            }
        }

        if (success) {
            feedbackMessage = ">>> Password copied to clipboard! (30s) <<<";
            pendingCleanupTask = new java.util.TimerTask() {
                @Override
                public void run() {
                    clearClipboardIfMatches(text, methodUsed[0]);
                    pendingCleanupTask = null;
                }
            };

            Runtime.getRuntime().addShutdownHook(new Thread(() -> clearClipboardIfMatches(text, methodUsed[0])));

            clipboardTimer.schedule(pendingCleanupTask, 30 * 1000);
        } else {
            feedbackMessage = "!!! Error: Clipboard unavailable. Install xclip on Linux !!!";
        }
    }

    private void clearClipboardIfMatches(String originalText, String methodUsed) {
        try {
            if ("AWT".equals(methodUsed)) {
                java.awt.datatransfer.Clipboard clipboard = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
                java.awt.datatransfer.Transferable contents = clipboard.getContents(null);
                if (contents != null && contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                    String currentContent = (String) contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor);
                    if (currentContent.equals(originalText)) {
                        clipboard.setContents(new java.awt.datatransfer.StringSelection(""), null);
                    }
                }
            } else if ("XCLIP".equals(methodUsed)) {
                Process checkProc = new ProcessBuilder("xclip", "-o", "-selection", "clipboard").start();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(checkProc.getInputStream()))) {
                    String currentContent = reader.lines().collect(java.util.stream.Collectors.joining("\n"));
                    if (currentContent.equals(originalText)) {
                        runXclipCopy("");
                    }
                }
            }
        } catch (Throwable e) {
            System.out.println("error here");
        }
    }

    private boolean runXclipCopy(String text) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("xclip", "-selection", "clipboard");
        Process process = pb.start();
        try (java.io.OutputStream osPipe = process.getOutputStream()) {
            osPipe.write(text.getBytes());
        }
        return process.waitFor() == 0;
    }
}