from PyQt4 import QtGui, uic

if __name__ == "__main__":
    import sys
    app = QtGui.QApplication(sys.argv)
    window = uic.loadUi("start.ui")
    window.show()

    sys.exit(app.exec_())