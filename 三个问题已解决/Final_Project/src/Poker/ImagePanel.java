package Poker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    private Image img;
    
    public ImagePanel(String img) {
        this(new ImageIcon(img).getImage());
    }
    
    public ImagePanel(Image img) {
        setImage(img);
    }
    
    public void setImage(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        revalidate(); // 通知布局管理器该组件已更改大小
        repaint(); // 请求重绘组件
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // 首先绘制面板的背景
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this); // 在调整大小后确保图像填满整个面板
        }
    }
    
    public void updateImage(Image newImg) {
        setImage(newImg);
    }
}
