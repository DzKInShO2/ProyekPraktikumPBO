import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class SettingsPanel extends JPanel {

    // --- State Variables ---
    private Image backgroundImage;
    private boolean isRemapping = false; 
    private JButton activeRemapButton = null; 

    // --- UI Components ---
    private JSlider bgmSlider;
    private JButton btnLeft, btnRight, btnJump;
    private JButton btnBack;
    // Tambahan:
    private JButton btnMute;
    private JButton btnTestSfx;
    private JButton btnApply;
    private JButton btnDefault;

    private String keyRight;
    private String keyLeft;
    private String keyJump;

    // --- Original BGM Volume = 1.0f
    private float originalVolume = 1.0f;

    // private float pendingSfxVolume = 1.0f;
    private boolean pendingMuted = false;

    // --- Colors & Fonts (Pixel Art Style) ---
    private final Color BORDER_COLOR = new Color(60, 40, 20); // Cokelat Tua/Hitam
    private final Color ACCENT_COLOR = new Color(255, 215, 0); // Kuning Emas
    // font label
    private final Font PIXEL_FONT = new Font("Monospaced", Font.BOLD, 16);
    // font section header
    private final Font SECTION_FONT = new Font("Monospaced", Font.BOLD, 22);

    public SettingsPanel(Runnable onBackAction) {
        setLayout(null);
        setFocusable(true);
        
        // sync awal volume dari SoundManager
        originalVolume = SoundManager.volume;
        // pendingSfxVolume = SoundManager.getVolume();   ini kalo nanti buat settings sfx nya
        pendingMuted = SoundManager.muted;
        
        // 1. Load Background
        try {
            backgroundImage = ImageIO.read(new File("res/PixelAdventure1Free/Background/settingsUI.png"));
        } catch (Exception e) {
            e.printStackTrace();
            setBackground(new Color(100, 149, 237)); // Fallback color (Sky Blue)
        }

        // 2. Event Listener untuk menangkap Key Press saat Remapping
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isRemapping && activeRemapButton != null) {
                    processRemap(e.getKeyCode(), KeyEvent.getKeyText(e.getKeyCode()));
                }
            }
        });

        // --- SETUP LAYOUT UTAMA ---
        int centerX = 600;
        int startY = 150;
        int panelWidth = 600;

        // --- SECTION 1: AUDIO ---
        createSectionLabel("AUDIO SETTINGS", centerX - 250, startY + 60);

        // MASTER VOLUME Slider 
        createLabel("Master Volume", centerX - 250, startY + 100);
        bgmSlider = createPixelSlider(centerX - 50, startY + 100);
        bgmSlider.setValue((int)(SoundManager.volume * 100));
        bgmSlider.addChangeListener(e -> {
            SoundManager.volume = bgmSlider.getValue() / 100f; 
        });
        add(bgmSlider);

        // Tombol MUTE di samping slider
        btnMute = new PixelButton(pendingMuted ? "UNMUTE" : "MUTE");
        btnMute.setBounds(centerX + 270, startY + 95, 100, 40);
        btnMute.addActionListener(e -> {
            pendingMuted = !pendingMuted;
            btnMute.setText(pendingMuted ? "UNMUTE" : "MUTE");
            SoundManager.muted = pendingMuted;
        });
        add(btnMute);

        // Tombol TEST SFX di bawah slider volume
        btnTestSfx = new PixelButton("TEST SOUND");
        btnTestSfx.setBounds(centerX, startY + 150, 200, 40);
        btnTestSfx.addActionListener(e -> {
            var res = ResourceManager.getInstance();
            SoundManager.playOnce(res.getJumpClip());
        });
        add(btnTestSfx);

        // --- SECTION 2: CONTROLS (Remapping) ---
        int controlY = startY + 230;
        createSectionLabel("CONTROLS SETUP", centerX - 250, controlY);

        var input = InputManager.getInstance();
        keyRight = input.getMoveRightString();
        keyLeft = input.getMoveLeftString();
        keyJump = input.getJumpString();

        createLabel("Move Left", centerX - 250, controlY + 40);
        btnLeft = createRemapButton(keyLeft, centerX, controlY + 35);
        btnLeft.addActionListener(e -> startRemapping(btnLeft));
        add(btnLeft);

        createLabel("Move Right", centerX - 250, controlY + 90);
        btnRight = createRemapButton(keyRight, centerX, controlY + 85);
        btnRight.addActionListener(e -> startRemapping(btnRight));
        add(btnRight);

        createLabel("Jump", centerX - 250, controlY + 140);
        btnJump = createRemapButton(keyJump, centerX, controlY + 135);
        btnJump.addActionListener(e -> startRemapping(btnJump));
        add(btnJump);

        // --- BACK BUTTON ---
        btnBack = new PixelButton("BACK TO MENU");
        btnBack.setBounds(centerX - 240, startY + 420, 180, 50);
        btnBack.addActionListener(e -> {
            // TIDAK menyimpan perubahan: kembali tanpa apply
            SoundManager.volume = originalVolume;
            if (onBackAction != null) onBackAction.run();
        });
        add(btnBack);

        // Tombol APPLY
        btnApply = new PixelButton("APPLY");
        btnApply.setBounds(centerX + 5, startY + 420, 140, 50);
        btnApply.addActionListener(e -> {
            SoundManager.muted = pendingMuted;

            applyKeyBindingsToRoot();

            JOptionPane.showMessageDialog(this, "Settings applied!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        add(btnApply);

        // Tombol SET DEFAULT
        btnDefault = new PixelButton("DEFAULT");
        btnDefault.setBounds(centerX + 230, startY + 420, 140, 50);
        btnDefault.addActionListener(e -> {
            keyLeft = "A";
            keyRight = "D";
            keyJump = "SPACE";

            SoundManager.volume = originalVolume;
            // pendingSfxVolume = 1.0f;
            pendingMuted = false;

            // update tampilan UI (belum diterapkan ke sistem sampai APPLY)
            bgmSlider.setValue(100);
            btnMute.setText("MUTE");

            btnLeft.setText(input.getMoveRightString());
            btnRight.setText(input.getMoveLeftString());
            btnJump.setText(input.getJumpString());
        });
        add(btnDefault);
    }

    // --- LOGIC KEY REMAPPING ---
    private void startRemapping(JButton targetBtn) {
        isRemapping = true;
        activeRemapButton = targetBtn;
        activeRemapButton.setText("PRESS KEY...");
        activeRemapButton.setBackground(new Color(255, 255, 120));
        this.requestFocusInWindow(); 
    }

    private void processRemap(int keyCode, String keyText) {
        // Hanya ubah nilai lokal, belum ke InputManager
        var rootPane = getRootPane();
        if (activeRemapButton == btnLeft) {
            keyLeft = keyText;
        } else if (activeRemapButton == btnRight) {
            keyRight = keyText;
        } else if (activeRemapButton == btnJump) {
            keyJump = keyText;
        }

        // Kembalikan tampilan tombol
        activeRemapButton.setText(keyText.toUpperCase());
        // Ganti dari Color.WHITE menjadi ACCENT_COLOR
        activeRemapButton.setBackground(ACCENT_COLOR);
        
        isRemapping = false;
        activeRemapButton = null;

        // TIDAK langsung apply ke sistem di sini;
        // akan diterapkan hanya ketika tombol APPLY diklik.
    }

    // Terapkan ulang key binding ke rootPane berdasarkan setting terbaru
    private void applyKeyBindingsToRoot() {
        JRootPane root = getRootPane();
        var input = InputManager.getInstance();
        input.setKeymap(root, keyRight, keyLeft, keyJump);
    }

    // Method untuk label biasa dengan stroke
    private void createLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw stroke/outline dulu
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                // ubah dari center ke rata kiri
                int textX = 2; 
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // Stroke hitam untuk kontras
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2.drawString(text, textX + dx, textY + dy);
                        }
                    }
                }
                
                // Draw text utama
                g2.setColor(ACCENT_COLOR);
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        lbl.setFont(PIXEL_FONT);
        lbl.setForeground(ACCENT_COLOR);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setBounds(x, y, 200, 30);
        add(lbl);
    }

    // Method untuk section header yang lebih besar dengan stroke
    private void createSectionLabel(String text, int x, int y) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw stroke/outline dulu
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                // ubah dari center ke rata kiri
                int textX = 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                // Stroke hitam lebih tebal untuk header
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int dx = -3; dx <= 3; dx++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2.drawString(text, textX + dx, textY + dy);
                        }
                    }
                }
                
                // Draw text utama
                g2.setColor(ACCENT_COLOR);
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        };
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(ACCENT_COLOR);
        lbl.setHorizontalAlignment(SwingConstants.LEFT); 
        lbl.setBounds(x, y, 300, 35);
        add(lbl);
    }

    private JSlider createPixelSlider(int x, int y) {
        JSlider slider = new JSlider(0, 100, 80);
        slider.setBounds(x, y, 300, 30);
        slider.setOpaque(false);
        slider.setUI(new PixelSliderUI(slider)); 
        return slider;
    }

    private JButton createRemapButton(String text, int x, int y) {
        JButton btn = new PixelButton(text);
        btn.setBounds(x, y, 160, 40);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 1. Gambar Background Full Screen
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // =========================================================
    // CUSTOM UI CLASSES (AGAR TAMPILAN SEPERTI GAMEPLAY/PIXEL)
    // =========================================================

    // 1. Custom Button yang Kotak & Datar (Flat Pixel Style)
    private class PixelButton extends JButton {
        public PixelButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(PIXEL_FONT);
            // teks tetap coklat tua agar kebaca di atas kuning
            setForeground(BORDER_COLOR);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            // set background default tombol ke kuning
            setBackground(ACCENT_COLOR);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            boolean pressed = getModel().isPressed();
            
            // SHADOW di belakang button
            g2.setColor(new Color(0, 0, 0, 80)); // shadow hitam semi-transparan
            g2.fillRect(4, 4, getWidth(), getHeight());
            
            // Background kuning (atau sedikit abu kalau ditekan)
            g2.setColor(getBackground() != null && getBackground() != new Color(238,238,238) ? getBackground() : ACCENT_COLOR);
            if (pressed) {
                g2.setColor(Color.LIGHT_GRAY);
            }
            
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Border Tebal
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(4));
            g2.drawRect(2, 2, getWidth()-4, getHeight()-4);

            super.paintComponent(g);
        }
    }

    // 2. Custom Slider yang Tebal (Blocky Style)
    private class PixelSliderUI extends BasicSliderUI {
        public PixelSliderUI(JSlider b) {
            super(b);
        }

        @Override
        public void paintTrack(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Rectangle t = trackRect;
            
            // Jalur Kosong (Gelap)
            g2.setColor(new Color(200, 180, 160)); 
            g2.fillRect(t.x, t.y + (t.height/2) - 6, t.width, 12);
            
            // Border Jalur
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(t.x, t.y + (t.height/2) - 6, t.width, 12);
        }

        @Override
        public void paintThumb(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            // Kotak Slider (Bukan bulat)
            g2.setColor(ACCENT_COLOR);
            g2.fillRect(thumbRect.x, thumbRect.y + (thumbRect.height/2) - 10, 20, 20);
            
            // Border Kotak
            g2.setColor(BORDER_COLOR);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(thumbRect.x, thumbRect.y + (thumbRect.height/2) - 10, 20, 20);
        }
    }
}
