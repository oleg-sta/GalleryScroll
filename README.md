# Android gallery photo scroller

## What is it?

This is android application, that shows all your photos in GridView. Photo gallery uses AsyncTask and LRU disk and memory caches to smoothly scroll photos. Demonstration of the application:

![Demo](./assets/galleryscroll.gif)

# Table of Contests

- [What is it?](#what-is-it)
- [Requirements](#requirements)
- [How it works?](#how-it-works)

## Requirements

To build this application the next is required:

* android sdk 30+

## How it works

### Problem
Main thread shouldn't be blocked. Converting from .jpg file or any other file to thumbnail picture is heavy task. That's why any converting should be done outside of main thread. This application could be used with any amount of images and any size of them.

### AsyncTask
AsyncTask could be used to start process outside of the main thread.

Note. (AsyncTask was deprecated from API level 30).

### Caching
There are two caching ways: storing thumbnails in the memory and storing them on the cache directory. Mechanism for the cache is LRU(least recently used).