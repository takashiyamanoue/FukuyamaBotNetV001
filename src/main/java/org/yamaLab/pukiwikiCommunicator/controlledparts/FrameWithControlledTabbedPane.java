/*
 * �쐬��: 2005/04/25
 *
 * ���̐������ꂽ�R�����g�̑}����e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
package org.yamaLab.pukiwikiCommunicator.controlledparts;

import java.awt.*;

/**
 * @author yamachan
 *
 * ���̐������ꂽ�R�����g�̑}����e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */
public interface FrameWithControlledTabbedPane {

	void changeStateOnTheTabbedPane(int id, int value);

	void exitMouseOnTheTabbedPane(int id);

	void enterMouseOnTheTabbedPane(int id);

	void sendEvent(String x);

	boolean isControlledByLocalUser();

	Color getBackground();

	void stateChangedAtTabbedPane(int id, int value);

	void mouseExitedAtTabbedPane(int id);

	void mouseEnteredAtTabbedPane(int id);


	//{{DECLARE_CONTROLS
	//}}

}