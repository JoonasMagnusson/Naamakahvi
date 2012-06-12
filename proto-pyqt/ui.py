import sys
from PyQt4 import QtGui, uic
from Start import StartingWindow

if __name__ == "__main__":
    app = QtGui.QApplication(sys.argv)
    window = StartingWindow()

    sys.exit(app.exec_())