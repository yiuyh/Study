from django.conf.urls import url

from Api import views

urlpatterns = [
    url(r'^books/$', views.books, name='books'),
    url(r'^books/(?P<bookid>\d+)/', views.book, name='book'),
]