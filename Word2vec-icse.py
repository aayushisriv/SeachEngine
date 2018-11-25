
# coding: utf-8

# In[94]:


from __future__ import absolute_import,division,print_function
import codecs
import glob
import logging
import multiprocessing
import os
import pprint
import re
import nltk
import gensim.models.word2vec as w2v
import sklearn.manifold
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
from sklearn.decomposition import PCA


# In[35]:


get_ipython().magic(u'pylab inline')


# In[36]:


logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)


# In[37]:


#process our data
#clean data
nltk.download("punkt")#pretrained tokenizer
nltk.download("stopwords")#words like,and,the,an,of,a


# In[38]:


#get the paper names,matching text files
book_filenames = sorted(glob.glob("C:\icse_id.txt"))


# In[39]:


print("Found papers:")
book_filenames


# In[40]:


corpus_raw = u""
for book_filename in book_filenames:
    print("Reading '{0}'...".format(book_filename))
    with codecs.open(book_filename, "r", "utf-8") as book_file:
        corpus_raw += book_file.read()
    print("Corpus is now {0} characters long".format(len(corpus_raw)))
    print()


# In[41]:


tokenizer = nltk.data.load('tokenizers/punkt/english.pickle')


# In[42]:


raw_sentences = tokenizer.tokenize(corpus_raw)


# In[43]:


def sentence_to_wordlist(raw):
    clean = re.sub("[^a-zA-Z]"," ", raw)
    words = clean.split()
    return words


# In[44]:


sentences = []
for raw_sentence in raw_sentences:
    if len(raw_sentence) > 0:
        sentences.append(sentence_to_wordlist(raw_sentence))


# In[45]:


print(raw_sentences[3])
print(sentence_to_wordlist(raw_sentences[5]))


# In[46]:


token_count = sum([len(sentence) for sentence in sentences])
print("The book corpus contains {0:,} tokens".format(token_count))


# In[47]:


#ONCE we have vectors
#step 3 - build model
#3 main tasks that vectors help with
#DISTANCE, SIMILARITY, RANKING

# Dimensionality of the resulting word vectors.
#more dimensions, more computationally expensive to train
#but also more accurate
#more dimensions = more generalized
num_features = 300
# Minimum word count threshold.
min_word_count = 3

# Number of threads to run in parallel.
#more workers, faster we train
num_workers = multiprocessing.cpu_count()

# Context window length.
context_size = 7

# Downsample setting for frequent words.
#0 - 1e-5 is good for this
downsampling = 1e-3

# Seed for the RNG, to make the results reproducible.
#random number generator
#deterministic, good for debugging
seed = 1


# In[48]:


thrones2vec = w2v.Word2Vec(
    sg=1,
    seed=seed,
    workers=num_workers,
    size=num_features,
    min_count=min_word_count,
    window=context_size,
    sample=downsampling
)


# In[53]:


thrones2vec.build_vocab(sentences)


# In[54]:


print("Word2Vec vocabulary length:", len(thrones2vec.wv.vocab))


# In[55]:


thrones2vec.train(sentences, total_examples=thrones2vec.corpus_count , epochs=100)


# In[57]:


if not os.path.exists(os.path.join("trained",'sample')):
    os.makedirs(os.path.join("trained",'sample'))


# In[58]:


thrones2vec.save(os.path.join("trained", "thrones2vec.w2v"))


# In[59]:


thrones2vec = w2v.Word2Vec.load(os.path.join("trained", "thrones2vec.w2v"))


# In[60]:


tsne = sklearn.manifold.TSNE(n_components=2, random_state=0)


# In[61]:


all_word_vectors_matrix = thrones2vec.wv.vectors


# In[63]:


all_word_vectors_matrix_2d = tsne.fit_transform(all_word_vectors_matrix)


# In[66]:


points = pd.DataFrame(
    [
        (word, coords[0], coords[1])
        for word, coords in [
            (word, all_word_vectors_matrix_2d[thrones2vec.wv.vocab[word].index])
            for word in thrones2vec.wv.vocab
        ]
    ],
    columns=["word", "x", "y"]
)


# In[67]:


points.head(10)


# In[68]:


sns.set_context("poster")


# In[69]:


ax=points.plot.scatter("x", "y", s=10, figsize=(20, 12))


# In[88]:


def plot_region(x_bounds, y_bounds):
    slice = points[
        (x_bounds[0] <= points.x) &
        (points.x <= x_bounds[1]) & 
        (y_bounds[0] <= points.y) &
        (points.y <= y_bounds[1])
    ]
    
    ax = slice.plot.scatter("x", "y", s=35, figsize=(10, 8))
    for i, point in slice.iterrows():
        ax.text(point.x, point.y, point.word, fontsize=11)


# In[90]:


plot_region(x_bounds=(4.0, 4.2), y_bounds=(-0.5, -0.1))


# In[99]:


X = thrones2vec[thrones2vec.wv.vocab]
pca = PCA(n_components=2)
result = pca.fit_transform(X)
pyplot.scatter(result[:, 0], result[:, 1])
words = list(thrones2vec.wv.vocab)
for i, word in enumerate(words):
        pyplot.annotate(word, xy=(result[i, 0], result[i, 1]))
pyplot.show()


# In[74]:


plot_region(x_bounds=(0, 1), y_bounds=(4, 4.5))


# In[77]:



thrones2vec.most_similar("knowledge")


# In[78]:


thrones2vec.most_similar("analysis")


# In[79]:


thrones2vec.most_similar("information")


# In[82]:


def nearest_similarity_cosmul(start1, end1, end2):
    similarities = thrones2vec.most_similar_cosmul(
        positive=[end2, start1],
        negative=[end1]
    )
    start2 = similarities[0][0]
    print("{start1} is related to {end1}, as {start2} is related to {end2}".format(**locals()))
    return start2


# In[87]:


nearest_similarity_cosmul("Curriculum", "Algorithms", "System")
nearest_similarity_cosmul("new", "Concurrent", "Framework")
nearest_similarity_cosmul("large", "software", "company")

