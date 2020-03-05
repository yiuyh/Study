# -*- coding: utf-8 -*-
# Generated by Django 1.11.7 on 2018-10-09 08:01
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Book',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('b_name', models.CharField(max_length=32)),
                ('b_price', models.FloatField(default=1)),
            ],
        ),
    ]
