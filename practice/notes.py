import pandas as pd
import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.tree import plot_tree

from sklearn.preprocessing import StandardScaler, MinMaxScaler, LabelEncoder, Normalizer
from imblearn.over_sampling import SMOTE
from sklearn.model_selection import train_test_split

from sklearn.feature_selection import VarianceThreshold, SelectKBest, chi2, mutual_info_classif, RFE
from sklearn.linear_model import Lasso, Ridge, ElasticNet

from sklearn.linear_model import LinearRegression
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.svm import SVC
from sklearn.neighbors import KNeighborsClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.cluster import KMeans
from xgboost import XGBClassifier
from sklearn.decomposition import PCA

from sklearn.ensemble import StackingClassifier
from sklearn.ensemble import VotingClassifier
from sklearn.model_selection import RandomizedSearchCV, GridSearchCV

from sklearn.metrics import auc, accuracy_score, confusion_matrix, precision_score, recall_score, f1_score, mean_squared_error, r2_score, classification_report, mean_absolute_error
from sklearn.metrics import roc_curve, roc_auc_score, precision_recall_curve


