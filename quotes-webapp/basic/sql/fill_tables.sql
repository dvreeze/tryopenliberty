
-- Subjects

INSERT INTO quote_schema.subject (subject_text)
VALUES ('inner strength');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('liberty');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('politics');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('financial system');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('defense');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('peace');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('patriotism');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('war');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('profit');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('genius');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('faith');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('achievements');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('conquest');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('racket');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('hubris');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('corrupt government');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('hidden knowledge');

INSERT INTO quote_schema.subject (subject_text)
VALUES ('truth');

-- Quotes

INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('If you can learn how to use your mind, anything is possible.', 'Wim Hof');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'If you can learn how to use your mind, anything is possible.'
   AND s.subject_text = 'inner strength';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('I''m not afraid of dying. I''m afraid not to have lived.', 'Wim Hof');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I''m not afraid of dying. I''m afraid not to have lived.'
   AND s.subject_text = 'inner strength';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('I''ve come to understand that if you want to learn something badly enough,\nyou''ll find a way to make it happen.\nHaving the will to search and succeed is very important', 'Wim Hof');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I''ve come to understand that if you want to learn something badly enough,\nyou''ll find a way to make it happen.\nHaving the will to search and succeed is very important'
   AND s.subject_text = 'inner strength';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('In nature, it is not only the physically weak but the mentally weak that get eaten.\nNow we have created this modern society in which we have every comfort,\nyet we are losing our ability to regulate our mood, our emotions.', 'Wim Hof');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'In nature, it is not only the physically weak but the mentally weak that get eaten.\nNow we have created this modern society in which we have every comfort,\nyet we are losing our ability to regulate our mood, our emotions.'
   AND s.subject_text = 'inner strength';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Cold is a stressor, so if you are able to get into the cold and control your body''s response to it,\nyou will be able to control stress.', 'Wim Hof');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Cold is a stressor, so if you are able to get into the cold and control your body''s response to it,\nyou will be able to control stress.'
   AND s.subject_text = 'inner strength';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Justifying conscription to promote the cause of liberty is one of the most bizarre notions ever conceived by man!\nForced servitude, with the risk of death and serious injury as a price to live free, makes no sense.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Justifying conscription to promote the cause of liberty is one of the most bizarre notions ever conceived by man!\nForced servitude, with the risk of death and serious injury as a price to live free, makes no sense.'
   AND s.subject_text = 'liberty';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('When the federal government spends more each year than it collects in tax revenues,\nit has three choices: It can raise taxes, print money, or borrow money.\nWhile these actions may benefit politicians, all three options are bad for average Americans.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'When the federal government spends more each year than it collects in tax revenues,\nit has three choices: It can raise taxes, print money, or borrow money.\nWhile these actions may benefit politicians, all three options are bad for average Americans.'
   AND s.subject_text = 'liberty';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Well, I don''t think we should go to the moon.\nI think we maybe should send some politicians up there.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Well, I don''t think we should go to the moon.\nI think we maybe should send some politicians up there.'
   AND s.subject_text = 'politics';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('I think a submarine is a very worthwhile weapon.\nI believe we can defend ourselves with submarines and all our troops back at home.\nThis whole idea that we have to be in 130 countries and 900 bases...\nis an old-fashioned idea.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I think a submarine is a very worthwhile weapon.\nI believe we can defend ourselves with submarines and all our troops back at home.\nThis whole idea that we have to be in 130 countries and 900 bases...\nis an old-fashioned idea.'
   AND s.subject_text = 'liberty';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Of course I''ve already taken a very modest position on the monetary system,\nI do take the position that we should just end the Fed.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Of course I''ve already taken a very modest position on the monetary system,\nI do take the position that we should just end the Fed.'
   AND s.subject_text = 'financial system';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Of course I''ve already taken a very modest position on the monetary system,\nI do take the position that we should just end the Fed.'
   AND s.subject_text = 'liberty';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Legitimate use of violence can only be that which is required in self-defense.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Legitimate use of violence can only be that which is required in self-defense.'
   AND s.subject_text = 'defense';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('I am absolutely opposed to a national ID card.\nThis is a total contradiction of what a free society is all about.\nThe purpose of government is to protect the secrecy and the privacy of all individuals,\nnot the secrecy of government. We don''t need a national ID card.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I am absolutely opposed to a national ID card.\nThis is a total contradiction of what a free society is all about.\nThe purpose of government is to protect the secrecy and the privacy of all individuals,\nnot the secrecy of government. We don''t need a national ID card.'
   AND s.subject_text = 'liberty';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Maybe we ought to consider a Golden Rule in foreign policy:\nDon''t do to other nations what we don''t want happening to us.\nWe endlessly bomb these countries and then we wonder why they get upset with us?', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Maybe we ought to consider a Golden Rule in foreign policy:\nDon''t do to other nations what we don''t want happening to us.\nWe endlessly bomb these countries and then we wonder why they get upset with us?'
   AND s.subject_text = 'liberty';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Maybe we ought to consider a Golden Rule in foreign policy:\nDon''t do to other nations what we don''t want happening to us.\nWe endlessly bomb these countries and then we wonder why they get upset with us?'
   AND s.subject_text = 'peace';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('I am just absolutely convinced that the best formula for giving us peace and\npreserving the American way of life is freedom, limited government,\nand minding our own business overseas.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I am just absolutely convinced that the best formula for giving us peace and\npreserving the American way of life is freedom, limited government,\nand minding our own business overseas.'
   AND s.subject_text = 'liberty';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I am just absolutely convinced that the best formula for giving us peace and\npreserving the American way of life is freedom, limited government,\nand minding our own business overseas.'
   AND s.subject_text = 'peace';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Real patriotism is a willingness to challenge the government when it''s wrong.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Real patriotism is a willingness to challenge the government when it''s wrong.'
   AND s.subject_text = 'liberty';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Real patriotism is a willingness to challenge the government when it''s wrong.'
   AND s.subject_text = 'patriotism';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Believe me, the intellectual revolution is going on,\nand that has to come first before you see the political changes.\nThat''s where I''m very optimistic.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Believe me, the intellectual revolution is going on,\nand that has to come first before you see the political changes.\nThat''s where I''m very optimistic.'
   AND s.subject_text = 'politics';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('War is never economically beneficial except for those in position to profit from war expenditures.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'War is never economically beneficial except for those in position to profit from war expenditures.'
   AND s.subject_text = 'war';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'War is never economically beneficial except for those in position to profit from war expenditures.'
   AND s.subject_text = 'profit';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('There is only one kind of freedom and that''s individual liberty.\nOur lives come from our creator and our liberty comes from our creator.\nIt has nothing to do with government granting it.', 'Ron Paul');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'There is only one kind of freedom and that''s individual liberty.\nOur lives come from our creator and our liberty comes from our creator.\nIt has nothing to do with government granting it.'
   AND s.subject_text = 'liberty';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Genius is patience', 'Isaac Newton');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Genius is patience'
   AND s.subject_text = 'genius';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Atheism is so senseless.\nWhen I look at the solar system,\nI see the earth at the right distance from the sun to receive the proper amounts of heat and light.\nThis did not happen by chance.', 'Isaac Newton');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Atheism is so senseless.\nWhen I look at the solar system,\nI see the earth at the right distance from the sun to receive the proper amounts of heat and light.\nThis did not happen by chance.'
   AND s.subject_text = 'faith';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('If I have seen further than others, it is by standing upon the shoulders of giants.', 'Isaac Newton');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'If I have seen further than others, it is by standing upon the shoulders of giants.'
   AND s.subject_text = 'achievements';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('WAR is a racket.\nIt always has been.\nIt is possibly the oldest, easily the most profitable, surely the most vicious.\nIt is the only one international in scope.\nIt is the only one in which the profits are reckoned in dollars and the losses in lives.', 'Smedley Butler');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'WAR is a racket.\nIt always has been.\nIt is possibly the oldest, easily the most profitable, surely the most vicious.\nIt is the only one international in scope.\nIt is the only one in which the profits are reckoned in dollars and the losses in lives.'
   AND s.subject_text = 'war';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('I spent thirty-three years and four months in active military service as a member of this country''s most agile military force,\nthe Marine Corps.\nI served in all commissioned ranks from Second Lieutenant to Major-General.\nAnd during that period, I spent most of my time being a high class muscle-man for Big Business, for Wall Street and for the Bankers.\nIn short, I was a racketeer, a gangster for capitalism.', 'Smedley Butler');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I spent thirty-three years and four months in active military service as a member of this country''s most agile military force,\nthe Marine Corps.\nI served in all commissioned ranks from Second Lieutenant to Major-General.\nAnd during that period, I spent most of my time being a high class muscle-man for Big Business, for Wall Street and for the Bankers.\nIn short, I was a racketeer, a gangster for capitalism.'
   AND s.subject_text = 'conquest';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I spent thirty-three years and four months in active military service as a member of this country''s most agile military force,\nthe Marine Corps.\nI served in all commissioned ranks from Second Lieutenant to Major-General.\nAnd during that period, I spent most of my time being a high class muscle-man for Big Business, for Wall Street and for the Bankers.\nIn short, I was a racketeer, a gangster for capitalism.'
   AND s.subject_text = 'racket';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'I spent thirty-three years and four months in active military service as a member of this country''s most agile military force,\nthe Marine Corps.\nI served in all commissioned ranks from Second Lieutenant to Major-General.\nAnd during that period, I spent most of my time being a high class muscle-man for Big Business, for Wall Street and for the Bankers.\nIn short, I was a racketeer, a gangster for capitalism.'
   AND s.subject_text = 'war';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Only those who would be called upon to risk their lives for their country should have the privilege of voting\nto determine whether the nation should go to war.', 'Smedley Butler');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Only those who would be called upon to risk their lives for their country should have the privilege of voting\nto determine whether the nation should go to war.'
   AND s.subject_text = 'war';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('The illegal we do immediately; the unconstitutional takes a little longer.', 'Henry Kissinger');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'The illegal we do immediately; the unconstitutional takes a little longer.'
   AND s.subject_text = 'corrupt government';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Military men are dumb, stupid animals to be used as pawns for foreign policy.', 'Henry Kissinger');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Military men are dumb, stupid animals to be used as pawns for foreign policy.'
   AND s.subject_text = 'hubris';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Military men are dumb, stupid animals to be used as pawns for foreign policy.'
   AND s.subject_text = 'corrupt government';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('Every now and again the United States has to pick up a crappy little country and throw it against a wall\njust to prove we are serious.', 'Michael Ledeen');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Every now and again the United States has to pick up a crappy little country and throw it against a wall\njust to prove we are serious.'
   AND s.subject_text = 'hubris';

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'Every now and again the United States has to pick up a crappy little country and throw it against a wall\njust to prove we are serious.'
   AND s.subject_text = 'war';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('We now have the technology to bring ET home.', 'Ben Rich');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'We now have the technology to bring ET home.'
   AND s.subject_text = 'hidden knowledge';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('If you want to find the secrets of the universe, think in terms of energy, frequency and vibration.', 'Nikola Tesla');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'If you want to find the secrets of the universe, think in terms of energy, frequency and vibration.'
   AND s.subject_text = 'hidden knowledge';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('The day science begins to study non-physical phenomena,\nit will make more progress in one decade than in all the previous centuries of its existence.', 'Nikola Tesla');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'The day science begins to study non-physical phenomena,\nit will make more progress in one decade than in all the previous centuries of its existence.'
   AND s.subject_text = 'hidden knowledge';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('If you tell the truth, you don''t have to remember anything.', 'Mark Twain');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'If you tell the truth, you don''t have to remember anything.'
   AND s.subject_text = 'truth';


INSERT INTO quote_schema.quote (quote_text, attributed_to)
VALUES ('The secret of getting ahead is getting started.', 'Mark Twain');

INSERT INTO quote_schema.quote_subject (quote_id, subject_id)
SELECT q.id, s.id
  FROM quote_schema.quote AS q, quote_schema.subject AS s
 WHERE q.quote_text = 'The secret of getting ahead is getting started.'
   AND s.subject_text = 'truth';
