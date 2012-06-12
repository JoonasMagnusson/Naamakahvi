from PyQt4 import uic
from NewUserPics import NewUserPicsWindow 

FormClass, BaseClass = uic.loadUiType("register.ui")


class RegistrationWindow(BaseClass, FormClass):
    def __init__(self, parent=None):
        super(BaseClass, self).__init__(parent)
         
        self.setupUi(self)
        self.show()

    def submitName(self):
        self.userPic = NewUserPicsWindow()
        self.close()