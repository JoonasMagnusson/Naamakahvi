from PyQt4 import uic


FormClass, BaseClass = uic.loadUiType("balance.ui")


class BalanceWindow(BaseClass, FormClass):
    def __init__(self, parent=None):
        super(BaseClass, self).__init__(parent)
         
        self.setupUi(self)
        self.show()

    def backToStart(self):
        self.close()