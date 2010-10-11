package trader;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.google.inject.Singleton;

@Singleton
public class TraderGui extends JPanel {

	/**
	 * Create the panel.
	 */
	public TraderGui() {

		JButton button = new JButton("New button");

		JButton button_1 = new JButton("New button");

		JButton button_2 = new JButton("New button");

		JButton button_3 = new JButton("New button");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				button)
																		.addPreferredGap(
																				ComponentPlacement.RELATED,
																				192,
																				Short.MAX_VALUE)
																		.addComponent(
																				button_1))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addComponent(
																				button_2)
																		.addPreferredGap(
																				ComponentPlacement.RELATED,
																				192,
																				Short.MAX_VALUE)
																		.addComponent(
																				button_3)))
										.addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(
				Alignment.LEADING)
				.addGroup(
						Alignment.TRAILING,
						groupLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										groupLayout
												.createParallelGroup(
														Alignment.BASELINE)
												.addComponent(button_2)
												.addComponent(button_3))
								.addPreferredGap(ComponentPlacement.RELATED,
										226, Short.MAX_VALUE)
								.addGroup(
										groupLayout
												.createParallelGroup(
														Alignment.BASELINE)
												.addComponent(button)
												.addComponent(button_1))
								.addContainerGap()));
		setLayout(groupLayout);

	}
}
