package org.ilite.frc.vision.camera.tools.overlayGenerator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

public class OverlayGeneratorCode {
    private final JPanel mView = new JPanel(new BorderLayout());

    public OverlayGeneratorCode() {
        JMenuBar aMenuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("FILE");

        JMenuItem load = new JMenuItem("Load Image");
        load.addActionListener(new ActionListener() {

            private BufferedImage mCurrentImage;

            @Override
            public void actionPerformed(ActionEvent pE) {
                JFileChooser aChooser = new JFileChooser();
                aChooser.setFileFilter(new FileFilter() {

                    @Override
                    public String getDescription() {
                        return "Images";
                    }

                    @Override
                    public boolean accept(File pF) {
                        return pF.isDirectory() || pF.getName().endsWith("png");
                    }
                });
                int aShowOpenDialog = aChooser.showOpenDialog(mView);
                if(aShowOpenDialog ==JFileChooser.APPROVE_OPTION) {
                    try {
                        mCurrentImage = ImageIO.read(aChooser.getSelectedFile());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        });

        aMenuBar.add(fileMenu);


        mView.add(aMenuBar, BorderLayout.NORTH);
    }

    public static final void createAndShowGUI()  {
        JFrame aFrame = new JFrame();
        OverlayGeneratorCode aCode = new OverlayGeneratorCode();
        aFrame.setContentPane(aCode.mView);
        aFrame.pack();
        aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        aFrame.setVisible(true);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();   
            }
        });
    }

}
