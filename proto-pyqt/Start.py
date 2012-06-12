from PyQt4 import uic
from Authentication import AuthenticationWindow

FormClass, BaseClass = uic.loadUiType("start.ui")

class StartingWindow(BaseClass, FormClass):
    def __init__(self, parent=None):
        super(BaseClass, self).__init__(parent)
         
        self.setupUi(self)
        self.show()

    def authenticateUser(self):
        self.auth = AuthenticationWindow()
        self.hide()