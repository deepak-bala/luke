package org.apache.lucene.luke.app.desktop.components.fragments.search;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.luke.app.desktop.components.ComponentOperatorRegistry;
import org.apache.lucene.luke.app.desktop.components.TabbedPaneProvider;
import org.apache.lucene.luke.app.desktop.components.util.FontUtil;
import org.apache.lucene.luke.app.desktop.util.MessageUtils;

import javax.annotation.Nonnull;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AnalyzerPaneProvider implements Provider<JScrollPane> {

  private final TabbedPaneProvider.TabSwitcherProxy tabSwitcher;

  private final JLabel analyzerNameLbl = new JLabel();

  private final JList<String> charFilterList = new JList<>();

  private final JTextField tokenizerTF = new JTextField();

  private final JList<String> tokenFilterList = new JList<>();

  private Analyzer analyzer = new StandardAnalyzer();

  class AnalyzerTabOperatorImpl implements AnalyzerTabOperator {

    @Override
    public Analyzer getCurrentAnalyzer() {
      return analyzer;
    }
  }

  @Inject
  public AnalyzerPaneProvider(TabbedPaneProvider.TabSwitcherProxy tabSwitcher,
                              ComponentOperatorRegistry operatorRegistry) {
    this.tabSwitcher = tabSwitcher;

    operatorRegistry.register(AnalyzerTabOperator.class, new AnalyzerTabOperatorImpl());
  }

  public void setAnalyzer(@Nonnull Analyzer analyzer) {
    this.analyzer = analyzer;
  }

  @Override
  public JScrollPane get() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

    panel.add(analyzerNamePane());
    panel.add(new JSeparator(JSeparator.HORIZONTAL));
    panel.add(analysisChanePane());

    tokenizerTF.setEditable(false);
    return new JScrollPane(panel);
  }

  private JPanel analyzerNamePane() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));

    panel.add(new JLabel(MessageUtils.getLocalizedMessage("search_analyzer.label.name")));

    analyzerNameLbl.setText(analyzer.getClass().getName());
    panel.add(analyzerNameLbl);

    JLabel changeLbl = new JLabel(MessageUtils.getLocalizedMessage("search_analyzer.hyperlink.change"));
    changeLbl.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        tabSwitcher.switchTab(TabbedPaneProvider.Tab.ANALYZER);
      }
    });
    panel.add(FontUtil.toLinkText(changeLbl));

    return panel;
  }

  private JPanel analysisChanePane() {
    JPanel panel = new JPanel(new BorderLayout());

    JPanel top = new JPanel(new FlowLayout(FlowLayout.LEADING));
    top.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    top.add(new JLabel(MessageUtils.getLocalizedMessage("search_analyzer.label.chain")));
    panel.add(top, BorderLayout.PAGE_START);

    JPanel center = new JPanel(new GridBagLayout());

    GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.BOTH;
    c.insets = new Insets(5, 5, 5, 5);

    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0.1;
    center.add(new JLabel(MessageUtils.getLocalizedMessage("search_analyzer.label.charfilters")), c);

    charFilterList.setVisibleRowCount(3);
    JScrollPane charFilterSP = new JScrollPane(charFilterList);
    c.gridx = 1;
    c.gridy = 0;
    c.weightx = 0.5;
    center.add(charFilterSP, c);

    c.gridx = 0;
    c.gridy = 1;
    c.weightx = 0.1;
    center.add(new JLabel(MessageUtils.getLocalizedMessage("search_analyzer.label.tokenizer")), c);

    tokenizerTF.setColumns(30);
    tokenizerTF.setPreferredSize(new Dimension(400, 25));
    tokenizerTF.setBorder(BorderFactory.createLineBorder(Color.gray));
    c.gridx = 1;
    c.gridy = 1;
    c.weightx = 0.5;
    center.add(tokenizerTF, c);

    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 0.1;
    center.add(new JLabel(MessageUtils.getLocalizedMessage("search_analyzer.label.tokenfilters")), c);

    tokenFilterList.setVisibleRowCount(3);
    JScrollPane tokenFilterSP = new JScrollPane(tokenFilterList);
    c.gridx = 1;
    c.gridy = 2;
    c.weightx = 0.5;
    center.add(tokenFilterSP, c);

    panel.add(center, BorderLayout.CENTER);

    return panel;
  }

  public interface AnalyzerTabOperator extends ComponentOperatorRegistry.ComponentOperator {
    Analyzer getCurrentAnalyzer();
  }
}