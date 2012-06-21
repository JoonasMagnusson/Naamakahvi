package naamakahvi.swingui;
import java.awt.*;

import javax.swing.*;

/*
 * Kälin erikoistoimintosivu
 */
public class ETPage extends JPanel{
	private IDButton cancel;
	private JTextArea text;
	
	public ETPage(CafeUI master){
		cancel = new IDButton(CafeUI.BUTTON_CANCEL, "Peruuta");
		cancel.setFont(CafeUI.UI_FONT_BIG);
		cancel.addActionListener(master);
		
		text = new JTextArea(""+
"                          '\n"+
"                        '   '\n"+
"                      '       '\n"+
"                 .  '  .        '                        '\n"+
"             '             '      '                   '   '\n"+
"          '                    '  . '              '      '\n"+
"       '                             .          '        '\n"+
"    '                                   '  . '         '\n"+
"  '                                                   .\n"+
".    ()     .                                        .\n"+
" .                                                    '\n"+
"   .        '  .'''.                    . . .           .\n"+
"      .    '   '....'               ..'.      ' .\n"+
"         '  .                     .     '          '     '\n"+
"               '  .  .  .  .  . '.    .'              '  .\n"+
"                   '         '    '. '\n"+
"                     '       '      '\n"+
"                       ' .  '\n"+
"                          '\n");
		text.setFont(CafeUI.UI_FONT);
		add(cancel);
        add(text);
	}

}
