
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
//import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Sudoku extends JPanel implements ActionListener {
	public JFrame jdialog;
	public JPanel mainPanel, keyPanel, btnPanel, northPanel, statePanel, scorePanel;
	public JLabel scoreLabel, stateLabel;
	public JButton[] keys;
	public JButton[][] cells;
	public JButton exitBtn;
	public int[][] gridValues = { { 0, 0, 0, 7, 2, 0, 0, 6, 4 }, { 2, 1, 0, 6, 0, 4, 0, 0, 0 },
			{ 6, 4, 0, 0, 0, 0, 3, 0, 8 }, { 0, 0, 6, 0, 1, 0, 9, 0, 2 }, { 0, 3, 0, 0, 7, 5, 8, 4, 0 },
			{ 4, 2, 0, 0, 0, 6, 0, 0, 1 }, { 0, 0, 4, 0, 0, 1, 2, 0, 9 }, { 0, 8, 0, 9, 0, 0, 6, 0, 0 },
			{ 0, 6, 2, 8, 3, 0, 4, 0, 0 } };;
	public int[][] startValues;
	public int[][] board;

	public final int SIZE = 9; // 행,열 칸 개수
	public final int BOX_SIZE = 3;

	int score = 0;
	String state = "게임 시작";
	int row = -1;
	int col = -1;

	int curNum;
	JButton clickedCell;
	int cellRow, cellCol;
	boolean keystate = false; // keys의 켜진 상태
	boolean cellstate = true; // 클릭한 칸의 정상적 완료상태

	// 수정
	JPanel[] sectors = new JPanel[9];

//	private JFrame frame;
//	private Mal mal;

	public Sudoku() {
		this.startValues = new int[SIZE][SIZE];
//		this.frame=frame;
//		this.mal=mal;
		generateBoard();
		initComponents();
		updateCells();
	}

	public void initComponents() {
		jdialog = new JFrame();
		// 수정
		// mainPanel = new JPanel(new GridLayout(9, 9));
		mainPanel = new JPanel(new GridLayout(3, 3));

		scoreLabel = new JLabel(Integer.toString(score));
		stateLabel = new JLabel(state);
		scorePanel = new JPanel();
		statePanel = new JPanel();
		scorePanel.add(scoreLabel);
		statePanel.add(stateLabel);
		northPanel = new JPanel(new FlowLayout());
		northPanel.add(scorePanel);
		northPanel.add(statePanel);
		// 1~9 와 백스페이스 버튼
		keyPanel = new JPanel(new FlowLayout());
		keys = new JButton[10];
		for (int i = 0; i < 9; i++) {
			keys[i] = new JButton(Integer.toString(i + 1));
			keys[i].addActionListener(this);
			keyPanel.add(keys[i]);
		}
		keys[9] = new JButton("Backspace");
		keys[9].addActionListener(this);
		keyPanel.add(keys[9]);
		keyToggle();

		cells = new JButton[9][9];
//		for (int i = 0; i < SIZE; i++) {
//			for (int j = 0; j < SIZE; j++) {
//				cells[i][j] = new JButton();
//				cells[i][j].setSize(50, 50);
//				cells[i][j].setFont(new Font("Arial", Font.BOLD, 30));
//				if (gridValues[i][j] == 0) {
//					cells[i][j].addActionListener(this);
//					cells[i][j].setForeground(Color.BLUE);
//				}
//				cells[i][j].setBackground(Color.WHITE);
//				mainPanel.add(cells[i][j]);
//			}
//		}

		// 수정
		for (int i = 0; i < 9; i++) {
			sectors[i] = new JPanel(new GridLayout(3, 3));
		}
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				int sectorNo = whichSector(i, j);
				cells[i][j] = new JButton();
				cells[i][j].setSize(50, 50);
				cells[i][j].setFont(new Font("Arial", Font.BOLD, 30));
				if (gridValues[i][j] == 0) {
					cells[i][j].addActionListener(this);
					cells[i][j].setForeground(Color.BLUE);
				}
				cells[i][j].setBackground(Color.WHITE);
				sectors[sectorNo].add(cells[i][j]);
			}
		}
		for (int i = 0; i < sectors.length; i++) {
			sectors[i].setBorder(BorderFactory.createLineBorder(Color.BLACK));
			mainPanel.add(sectors[i]);
		}

		btnPanel = new JPanel();
//		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
		exitBtn = new JButton("부루마블로 복귀");
		exitBtn.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
//				mal.setMoney(mal.getMoney()+score);
				JOptionPane.showMessageDialog(null, score + "원 획득하셨습니다.");
				jdialog.dispose();
			}

		});
		btnPanel.add("North", keyPanel);
		btnPanel.add("South", exitBtn);
		jdialog.getContentPane().add("North", northPanel);
		jdialog.getContentPane().add("South", btnPanel);
		jdialog.getContentPane().add("Center", mainPanel);
		jdialog.setSize(700, 700);
		jdialog.setVisible(true);
		jdialog.setResizable(false);
	}

	// 수정
	public int whichSector(int i, int j) {
		int horGroup = i / 3;
		int verGroup = j / 3;
		return horGroup * 3 + verGroup;
	}

	public void generateBoard() {
		for (int i = 0; i < 3; i++) {
			int row = generateRandom() - 1;
			switchRow(row);
			int col = generateRandom() - 1;
			switchCol(col);
		}
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				startValues[i][j] = gridValues[i][j];
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// cells action
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (e.getSource() == cells[i][j] && startValues[i][j] == 0) {
					if (cellstate) {
						keyToggle();
						if (!keystate) { // 정상적으로 클릭해서 keys가 켜질 경우
							row = i;
							col = j;
							cells[i][j].setBackground(Color.YELLOW);
							for (int i2 = 0; i2 < SIZE; i2++) {
								for (int j2 = 0; j2 < SIZE; j2++) {
									if (!(i2 == row && j2 == col)) {
										cells[i2][j2].setBackground(Color.WHITE);
									}
								}
							}
							cellstate = false;
						} else if (i == row && j == col) { // 재클릭하면 하이라이트 해제
							cells[i][j].setBackground(Color.WHITE);
							cellstate = true;
						}
					} else if (i == row && j == col) { // 재클릭하면 현재 칸 선택상태 해제
						cellstate = true;
						if (keystate) {
							keyToggle();
						}
					}
				}
			}
		}
		// keys action
		for (int i = 0; i < keys.length; i++) {
			if (e.getSource() == keys[i]) {
				curNum = i + 1;
				cellstate = actionUpdate(row, col);
				break;
			}
			if (e.getSource() == keys[9]) {
				curNum = -1;
				cellstate = actionUpdate(row, col);
				break;
			}
		}
	}

	public boolean actionUpdate(int row, int col) {
		if (row != -1 && curNum != 0 && curNum != -1 && gridValues[row][col] == 0) {
			if (isUnique(curNum, row, col)) {
				gridValues[row][col] = curNum;
				updateCells();
				score += 20;
				state = "맞았습니다! 20점 추가";
				scoreLabel.setText(Integer.toString(score));
				stateLabel.setText(state);
				keyToggle();
				cells[row][col].setBackground(Color.WHITE);
				curNum = 0;
			} else {
				state = "다시 시도하세요";
				stateLabel.setText(state);
				keyToggle();
				cells[row][col].setBackground(Color.WHITE);
				curNum = 0;
			}
		} else if (curNum == -1) {
			score -= 30;
			state = "취소는 30점 차감입니다 :(";
			stateLabel.setText(state);
			scoreLabel.setText(Integer.toString(score));
			gridValues[row][col] = 0;
			updateCells();
			keyToggle();
			cells[row][col].setText(null);
			cells[row][col].setBackground(Color.WHITE);
			curNum = 0;
		}
		return true;
	}

	// keystate이 true일 때 1-9 버튼 활성화하고 keystate을 반대로 설정
	public void keyToggle() {
		for (int i = 0; i < keys.length; i++) {
			keys[i].setEnabled(keystate);
		}
		keystate = !keystate;
	}

	// 각 칸의 값 jbutton에 반영
	public void updateCells() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (gridValues[i][j] != 0) {
					cells[i][j].setText(Integer.toString(gridValues[i][j]));
				}
			}
		}
	}

	public int generateRandom() {
		return (int) (Math.random() * 9) + 1;
	}

	public int generateRandom(int row, int col) {
		HashSet<Integer> possibleSet = possibleNumSet(row, col);
		if (possibleSet.isEmpty()) {
			return -1;
		}
		Object[] temp = possibleSet.toArray();
		int[] array = new int[possibleSet.size()];
		int i = 0;
		for (Object o : temp) {
			array[i++] = (int) o;
		}
		return array[(int) (Math.random() * array.length)];
	}

	public int switchRow(int row) {
		int section = row / 3;
		int changeRow = row;
		while (row == changeRow) {
			changeRow = section * 3 + (int) (Math.random() * 3);
		}
		int[] temp = new int[9];
		for (int j = 0; j < SIZE; j++) {
			temp[j] = gridValues[row][j];
			gridValues[row][j] = gridValues[changeRow][j];
			gridValues[changeRow][j] = temp[j];
		}
		return changeRow;
	}

	public int switchCol(int col) {
		int section = col / 3;
		int changeCol = col;
		while (col == changeCol) {
			changeCol = section * 3 + (int) (Math.random() * 3);
		}
		int[] temp = new int[9];
		for (int i = 0; i < SIZE; i++) {
			temp[i] = gridValues[i][col];
			gridValues[i][col] = gridValues[i][changeCol];
			gridValues[i][changeCol] = temp[i];
		}
		return changeCol;
	}

	public int arrayToNum(int[] position) {
		int cellno = position[0] * 9 + position[1];
		return cellno;
	}

	public int[] nextCell(int row, int col) {
		if (!(row == 8 && col == 8)) {
			if (col == 8) {
				row += 1;
				col = 0;
			} else {
				col += 1;
			}
		}
		int[] position = { row, col };
		return position;
	}

	public int[] previousCell(int row, int col) {
		if (!(row == 0 && col == 0)) {
			if (col == 0) {
				row -= 1;
				col = 8;
			} else {
				col -= 1;
			}
		}
		int[] position = { row, col };
		return position;
	}

//	public HashSet<Integer> possibleNumSet(HashSet<Integer> currentSet) {
//		HashSet<Integer> possibleSet = new HashSet<Integer>();
//		for (int i = 1; i <= 9; i++) {
//			possibleSet.add(i);
//		}
//		for (Integer i : currentSet) {
//			if (!currentSet.contains(i)) {
//				possibleSet.remove(i);
//			}
//		}
//		return possibleSet;
//	}

	public HashSet<Integer> possibleNumSet(int row, int col) {
//		HashSet<Integer> usedSet = new HashSet<Integer>();
		HashSet<Integer> possibleSet = new HashSet<Integer>();
		for (int num = 1; num <= 9; num++) {
			if (isUnique(num, row, col))
				possibleSet.add(num);
		}
		return possibleSet;
	}

	public boolean isUnique(int num, int i, int j) {
		return (unusedInBox(num, i, j) && unusedInCol(num, i, j) && unusedInRow(num, i, j));
	}

//	(row,col에 원소 추가 전 실행)
	public boolean unusedInBox(int num, int row, int col) {
		int startRow = row / 3;
		int startCol = col / 3;
		HashSet<Integer> check = new HashSet<Integer>();
		// 해당 박스에서 빈 칸이 아니면 숫자를 check에 추가
		for (int i = startRow * 3; i < startRow * 3 + BOX_SIZE; i++) {
			for (int j = startCol * 3; j < startCol * 3 + BOX_SIZE; j++) {
				if (!(row == i && col == j) && gridValues[i][j] != 0) {
					check.add(gridValues[i][j]);
				}
			}
		}
		if (check.contains(num)) {
			return false;
		}
		return true;
	}

	public boolean unusedInRow(int num, int row, int col) {
		for (int j = 0; j < SIZE; j++) {
			if (col != j && num == gridValues[row][j]) {
				return false;
			}
		}
		return true;
	}

	public boolean unusedInCol(int num, int row, int col) {
		for (int i = 0; i < SIZE; i++) {
			if (row != i && num == gridValues[i][col]) {
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		new Sudoku();
	}

}
